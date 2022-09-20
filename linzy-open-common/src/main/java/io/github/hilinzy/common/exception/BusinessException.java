package io.github.hilinzy.common.exception;

import cn.hutool.core.text.StrFormatter;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description  自定义业务异常
 * @author linzy
 * @date  2022/9/20
 **/
public class BusinessException extends RuntimeException{

  private static final long serialVersionUID = 1L;

  private ErrorCode errorCode;

  private Integer code;
  private String message;

  @Override
  public String getMessage() {
    return message;
  }
  public Integer getCode() {
    return code;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  /* ------------------------------------------------------------------------------------------- */

  public BusinessException(String message) {
    super(message);
    this.message = message;
  }

  public BusinessException(String message, Integer code) {
    this.code = code;
    this.message = message;
  }

  public BusinessException(String message, Throwable e) {
    super(message, e);
    this.message = message;
  }

  //异常信息国际化
  public BusinessException(ErrorCode errorCode, Object... args){

    this.errorCode = errorCode;
    this.code = errorCode.getCode();

    //暂时不接国际化
    this.message = errorCode.getMessage();

    if(args.length > 0){
      //占位符解析  not found {} , 22
      this.message = StrFormatter.format(message,args);
    }
  }
}
