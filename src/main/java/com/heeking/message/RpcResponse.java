package com.heeking.message;
/**
 * rpc 响应
 * @author zss
 */
public class RpcResponse {
	//响应的消息id
    private String responseId;
    //请求的消息id
    private String requestId;
    // 响应的消息是否成功
    private boolean success;
    // 响应的数据结果
    private Object result;
    // 如果有异常信息,在该对象中记录异常信息
    private Throwable throwable;
	public String getResponseId() {
		return responseId;
	}
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
    
    
}
