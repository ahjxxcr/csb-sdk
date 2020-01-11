package com.alibaba.csb.ws.sdk;import com.alibaba.csb.sdk.Version;import org.apache.commons.cli.*;import org.apache.commons.io.FileUtils;import javax.xml.soap.SOAPMessage;import javax.xml.ws.Dispatch;import java.io.File;import java.util.Arrays;import java.util.HashMap;import java.util.Map;public class CmdWsCaller {    private static final String SDK_VERSION = "1.1.5.3";    private static void usage(Options opt) {        HelpFormatter formatter = new HelpFormatter();        formatter.printHelp("java -jar wsclient.jar [options...]", opt);        System.out.println("\ncurrent SDK version:" + SDK_VERSION + "\n----");        Version.version();    }    private static boolean isEmpty1(String str, String msg) {        if (isEmpty(str)) {            System.out.println("请输入参数 " + msg);            return true;        }        return false;    }    private static boolean isEmpty(String str) {        return str == null || str.isEmpty();    }    private static void print(boolean debug, String format, Object args) {        if (!debug) {            return;        }        System.out.println(String.format(format, args));    }    /**     * 使用Dispatch方式发送soap请求调用WS     *     * @param params     * @param ns     * @param sname     * @param pname     * @param isSoap12     * @param ea     * @param reqSoap     * @param requestHeaders     * @throws Exception     */    private static void invokeWithDispath(WSParams params, String ns, String sname,                                          String pname, String soapActionUri, boolean isSoap12, String ea, String reqSoap, Map<String, String> requestHeaders) throws Exception {        Dispatch<SOAPMessage> dispatch = WSInvoker.createDispatch(params, ns, sname, pname, soapActionUri, isSoap12, ea);        SOAPMessage request = WSInvoker.createSOAPMessage(isSoap12, reqSoap);        WSInvoker.setHttpHeaders(dispatch, requestHeaders);        int code = 200;        String msg = null;        SOAPMessage reply = null;        long startTime = System.currentTimeMillis();        try {            reply = dispatch.invoke(request);        } catch (Exception e) {            code = 500;            msg = e.getMessage();            throw e;        } finally {            WSInvoker.log(params, startTime, ea, sname, code, msg);        }        String response = DumpSoapUtil.dumpSoapMessage(reply);        if (response != null)            System.out.println("\n-- 调用返回:\n" + response);        else            System.out.println("\n-- 调用返回为空");        //call multi-times for stress or flow-ctrl testing        int times = Integer.getInteger("test.stress.times", 0);        for (int i = 2; i <= times; i++) {            reply = dispatch.invoke(request);            ;            System.out.println("---- [#" + i + "] times call it for stress testing");            if (reply != null)                System.out.println("\n-- 调用返回:\n" + DumpSoapUtil.dumpSoapMessage(reply));            else                System.out.println("\n-- 调用返回为空");        }    }    //			"-api"//			"item.hsf.add"//			"-version"//			"1.0.0"//			"-bizIdKey"//			"bizid"//			"-bizId"//			"e48ffd7c1e7f4d07b7fc141f43503cb2"//			"http://csb.target.server:9081/item.hsf.add/1.0.0/add?wsdl"//			"-ea"//			"http://csb.target.server:9081/item.hsf.add/1.0.0/add"//			"-ns"//			"http://itemcenter.carshop.edas.alibaba.com/"//			"-sname"//			"item.hsf.add"//			"-pname"//			"addPortType"//			"-rd"//			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:test=\"http://itemcenter.carshop.edas.alibaba.com/\"><soapenv:Header/><soapenv:Body><test:add><item><itemName>mac book pro</itemName><quantity>5</quantity></item></test:add></soapenv:Body></soapenv:Envelope>"    public static void main(String[] args) {        Options opt = new Options();        opt.addOption("ak", true, "accessKey, 可选");        opt.addOption("sk", true, "secretKey, 可选");        opt.addOption("api", true, "服务名, 可选");        opt.addOption("version", true, "服务版本, 可选");		opt.addOption("wa", true, "wsdl地址，e.g: http://broker-ip:9081/api/version/method?wsdl");        opt.addOption("ea", true, "endpoint地址，e.g: http://broker-ip:9081/api/version/method");        opt.addOption("ns", true, "在wsdl中定义的服务的target namespace");        opt.addOption("sname", "serviceName", true, "在wsdl中定义的服务名");        opt.addOption("pname", "portName", true, "在wsdl中定义的端口名");        opt.addOption("action", "SOAPAction", true, "SOAPAction值");        opt.addOption("soap12", false, "-soap12 为soap12调用, 不定义为soap11");        opt.addOption("nonce", false, "-nonce 是否做nonce防重放处理，不定义为不做nonce重放处理");        opt.addOption("skipTimestamp", false, "-skipTimestamp 不设置时间戳，默认是设置");        opt.addOption("fingerStr", true, "签名指纹串, 可选");        opt.addOption("H", true, "http header, 格式: -H \"key:value\"");        opt.addOption("h", "help", false, "打印帮助信息");        opt.addOption("d", "debug", false, "打印调试信息");        opt.addOption("rf", true, "soap请求文件，文件里存储soap请求的Message格式内容");        opt.addOption("rd", true, "soap请求内容(Message)，如果设置该选项时，-rf选项被忽略");        opt.addOption("sdkv", "sdk-version", false, "sdk版本");        opt.addOption("bizIdKey", true, "业务id Key");        opt.addOption("bizId", true, "业务id");        CommandLineParser parser = new DefaultParser();        Boolean isDebug = false;        try {            CommandLine commandline = parser.parse(opt, args);            if (commandline.hasOption("h") || commandline.getOptions().length == 0) {                usage(opt);                return;            }            String ak = commandline.getOptionValue("ak");            String sk = commandline.getOptionValue("sk");            String api = commandline.getOptionValue("api");            String version = commandline.getOptionValue("version");            Boolean sdkv = commandline.hasOption("sdkv");            String ea = commandline.getOptionValue("ea");            String ns = commandline.getOptionValue("ns");            String sname = commandline.getOptionValue("sname");            String pname = commandline.getOptionValue("pname");            String action = commandline.getOptionValue("action");            String rf = commandline.getOptionValue("rf");            String rd = commandline.getOptionValue("rd");            String fingerStr = commandline.getOptionValue("fingerStr");            String[] headers = commandline.getOptionValues("H");            boolean isSoap12 = commandline.hasOption("soap12");            boolean nonce = commandline.hasOption("nonce");            boolean skipTimestamp = commandline.hasOption("skipTimestamp");            isDebug = commandline.hasOption("d");            String bizIdKey = commandline.getOptionValue("bizIdKey");            String bizId = commandline.getOptionValue("bizId");            if (sdkv) {                Version.version();                return;            }            if (isDebug) {                // printParams();                System.out.println("ak=" + ak);                System.out.println("sk=" + sk);                System.out.println("api=" + api);                System.out.println("version=" + version);                System.out.println("bizIdKey=" + bizIdKey);                System.out.println("bizId=" + bizId);                System.out.println("isSoap12=" + isSoap12);                System.out.println("nonce=" + nonce);                System.out.println("ea=" + ea);                System.out.println("ns=" + ns);                System.out.println("sname=" + sname);                System.out.println("pname=" + pname);                System.out.println("action=" + action);                System.out.println("headers=" + Arrays.toString(headers));                System.out.println("rd=" + rd);                if (isEmpty(rd)) {                    System.out.println("rf=" + rf);                }                System.out.println("nonce=" + nonce);                System.out.println("skipTimestamp=" + skipTimestamp);                System.out.println("fingerStr=" + fingerStr);            }            if (isEmpty1(ea, "-ea endpoint地址")) {                usage(opt);                return;            }            if (isEmpty1(ea, "-ns namespace")) {                usage(opt);                return;            }            if (isEmpty1(ea, "-sname serviceName")) {                usage(opt);                return;            }            if (isEmpty1(pname, "-pname portName")) {                usage(opt);                return;            }            if (isEmpty1(pname, "-action SOAPAction")) {                usage(opt);                return;            }            if (isEmpty(rf) && isEmpty(rd)) {                System.out.println("-rf 或 -rd 参数没有定义");                usage(opt);                return;            }            String reqData = (isEmpty(rd)) ? FileUtils.readFileToString(new File(rf)) : rd;            print(isDebug, "-- 请求报文: \n%s\n", reqData);            if (isEmpty(reqData)) {                print(true, "-- 操作失败：文件%s请求报文为空", rf);                return;            }            if (bizIdKey != null && !bizIdKey.trim().equals("")) {                WSClientSDK.bizIdKey(bizIdKey);            }            WSParams params = WSParams.create().accessKey(ak).secretKey(sk).fingerPrinter(fingerStr)                    .api(api).bizId(bizId).version(version).nonce(nonce).timestamp(!skipTimestamp).debug(isDebug);            Map<String, String> httpHeaders = new HashMap<String, String>();            if (headers != null) {                for (String header : headers) {                    String[] kv = header.split(":", 2);                    if (kv == null || kv.length != 2) {                        System.out.println("错误的HTTP头定义 正确格式: -H \"key:value\" !!" + header);                        return;                    }                    httpHeaders.put(kv[0], kv[1]);                }            }            invokeWithDispath(params, ns, sname, pname, action, isSoap12, ea, reqData, httpHeaders);        } catch (Exception e) {            System.out.println("-- 操作失败：" + e.getMessage());            if (isDebug)                e.printStackTrace(System.out);        }    }}