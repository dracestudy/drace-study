// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class EncodedValue implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public EncodedValue() {
  }

  /**
   * Construct from a JSON string.
   */
  public EncodedValue(String s) throws JSONException {
    this(new JSONObject(s));
  }

  EncodedValue(JSONObject jo) throws JSONException {
    value = new LocalVarValue(jo.get("value"));
    bstart = jo.getInt("bstart");
    bend = jo.getInt("bend");

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (value != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"value\":");
      value.toJsonBuffer(_out);
    }
    else
      throw new JSONException("Uninitialized field value");

    if (bstart != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"bstart\":");
      _out.append(String.valueOf(bstart));
    }
    else
      throw new JSONException("Uninitialized field bstart");

    if (bend != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"bend\":");
      _out.append(String.valueOf(bend));
    }
    else
      throw new JSONException("Uninitialized field bend");

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public LocalVarValue value;
  public Integer bstart;
  public Integer bend;
}
