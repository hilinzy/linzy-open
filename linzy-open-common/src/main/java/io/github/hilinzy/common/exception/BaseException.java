package io.github.hilinzy.common.exception;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description  基础错误
 * @author linzy
 * @date  2022/9/20
 **/
public class BaseException extends RuntimeException{

  private static final long serialVersionUID = 1L;
  private final String message;

  @Override
  public String getMessage() {
    return message;
  }

  public BaseException(String message) {
    super(message);
    this.message = message;
  }

}
