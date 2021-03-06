/*
 * Copyright (c) 2019.
 */

package com.alibaba.csb.selfDefProcess.udfmp;

import com.alibaba.csb.BaseSelfDefProcess;
import com.alibaba.csb.selfDefProcess.SelfDefProcessException;

import java.util.Map;

/**
 * 开放服务自定义消息拦截器
 */
public interface ServerMessageProcessInterceptor extends BaseSelfDefProcess {
    /**
     * 请求消息处理:收到客户请求后调用。用户可以：
     * <ul>
     * <li>  增加、修改、删除：请求头</li>
     * <li>  修改：后端接入服务的url</li>
     * <li>  增加、修改、删除：query参数</li>
     * <li>  修改：通过 contextMap.put(RESPONSE_BODY,body)，达到修改body的目标。如果是form请求，则直接body是map《String，List《String》》。如果是非form的文本请求，则body是String。其它请求，则是InputStream或byte[]对象</li>
     * <li>  保存自定义数据到服务处理上下文：直接put("_self_ {@link com.alibaba.csb.BaseSelfDefProcess#SELF_CONTEXT_PREFIX} 前缀的key",自定义value)</li>
     * <li>  直接中止处理流程：comtextMap设置了直RESPONSE_BODY，则会中止后续处理，直接将 RESPONSE_BODY 返回给CSB客户端（同时可设置响应消息头 RESPONSE_HEADERS，参见示例说明）</li>
     * <li>  抛出异常，以中止服务处理，异常消息将直接返回给CSB客户端</li>
     * </ul>
     *
     * @param contextMap 服务请求上下文信息map，各信息的key见 BaseSelfDefProcess 常量定义:
     *                   <ul>
     *                   <li> _biz_id {@link com.alibaba.csb.BaseSelfDefProcess#BIZ_ID}</li>
     *                   <li> _inner_ecsb_trace_id {@link com.alibaba.csb.BaseSelfDefProcess#TRACE_ID}</li>
     *                   <li> _csb_internal_name_  {@link com.alibaba.csb.BaseSelfDefProcess#CSB_INTERNAL_NAME}</li>
     *                   <li>_csb_broker_ip  {@link com.alibaba.csb.BaseSelfDefProcess#CSB_BROKER_IP}</li>
     *                   <li>_api_name  {@link com.alibaba.csb.BaseSelfDefProcess#API_NAME}</li>
     *                   <li>_api_version  {@link com.alibaba.csb.BaseSelfDefProcess#API_VERION}</li>
     *                   <li>_api_group  {@link com.alibaba.csb.BaseSelfDefProcess#API_GROUP}</li>
     *                   <li>userId  {@link com.alibaba.csb.BaseSelfDefProcess#USER_ID}</li>
     *                   <li>credentail_name  {@link com.alibaba.csb.BaseSelfDefProcess#CREDENTIAL_NAME}</li>
     *                   <li>_api_access_key  {@link com.alibaba.csb.BaseSelfDefProcess#ACCESS_KEY}</li>
     *                   <li>_remote_peer_ip  {@link com.alibaba.csb.BaseSelfDefProcess#REMOTE_PEER_IP}</li>
     *                   <li>_remote_real_ip  {@link com.alibaba.csb.BaseSelfDefProcess#BACKEND_REAL_IP}</li>
     *                   <li>server_protoco {@link com.alibaba.csb.BaseSelfDefProcess#SERVER_PROTOCO}</li>
     *                   <li>backend_Protoco {@link com.alibaba.csb.BaseSelfDefProcess#BACKEND_PROTOCO}</li>
     *                   <li>backend_url  {@link com.alibaba.csb.BaseSelfDefProcess#BACKEND_URL}</li>
     *                   <li>request_http_querys {@link com.alibaba.csb.BaseSelfDefProcess#REQUEST_HTTP_QUERYS}</li>
     *                   <li>request_headers  {@link com.alibaba.csb.BaseSelfDefProcess#REQUEST_HEADERS}</li>
     *                   <li>request_body  {@link com.alibaba.csb.BaseSelfDefProcess#REQUEST_BODY}</li>
     *                   </ul>
     */
    void requestProcess(Map<String, Object> contextMap) throws SelfDefProcessException;

    /**
     * 响应消息处理:向客户发送响应结果之前调用
     * <ul>
     * <li>  增加、修改、删除：响应头</li>
     * <li>  修改：通过 contextMap.put(RESPONSE_BODY,body)，达到修改body的目标。如果响应是文本，则是String对象。否则就是InputStream或byte[]对象。</li>
     * <li>  抛出异常，以中止服务处理，异常消息将直接返回给CSB客户端</li>
     * </ul>
     *
     * @param contextMap 服务请求上下文信息map，各信息的key见 BaseSelfDefProcess 常量定义:
     *                   <ul>
     *                   <li> _biz_id {@link com.alibaba.csb.BaseSelfDefProcess#BIZ_ID}</li>
     *                   <li> _inner_ecsb_trace_id {@link BaseSelfDefProcess#TRACE_ID}</li>
     *                   <li> _csb_internal_name_  {@link BaseSelfDefProcess#CSB_INTERNAL_NAME}</li>
     *                   <li>_csb_broker_ip  {@link BaseSelfDefProcess#CSB_BROKER_IP}</li>
     *                   <li>_api_name  {@link BaseSelfDefProcess#API_NAME}</li>
     *                   <li>_api_version  {@link BaseSelfDefProcess#API_VERION}</li>
     *                   <li>_api_group  {@link BaseSelfDefProcess#API_GROUP}</li>
     *                   <li>_api_owner_id  {@link BaseSelfDefProcess#API_OWNER_ID}</li>
     *                   <li>userId  {@link BaseSelfDefProcess#USER_ID}</li>
     *                   <li>credentail_name  {@link BaseSelfDefProcess#CREDENTIAL_NAME}</li>
     *                   <li>_api_access_key  {@link BaseSelfDefProcess#ACCESS_KEY}</li>
     *                   <li>_remote_peer_ip  {@link BaseSelfDefProcess#REMOTE_PEER_IP}</li>
     *                   <li>_remote_real_ip  {@link com.alibaba.csb.BaseSelfDefProcess#BACKEND_REAL_IP}</li>
     *                   <li>response_headers  {@link BaseSelfDefProcess#RESPONSE_HEADERS}</li>
     *                   <li>response_body  {@link BaseSelfDefProcess#RESPONSE_BODY}</li>
     *                   <li>response_exception  {@link BaseSelfDefProcess#RESPONSE_EXCEPTION}</li>
     *                   </ul>
     */
    void responseProcess(Map<String, Object> contextMap) throws SelfDefProcessException;
}
