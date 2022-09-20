package io.github.hilinzy.common.exception;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description 业务逻辑常量类
 * @author linzy
 * @date  2022/9/20
 **/
public enum ErrorCode {

  /**
   * 2XXX
   */
  SUCCESS( 200, "success", "success");

  private Boolean isi18n;

  private Integer code;
  private String message;
  private String i18nCode;

  ErrorCode(Integer code, String message, String i18nCode) {
    this.code = code;
    this.message = message;
    this.i18nCode = i18nCode;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getI18nCode() {
    return i18nCode;
  }

  public void setI18nCode(String i18nCode) {
    this.i18nCode = i18nCode;
  }
}
