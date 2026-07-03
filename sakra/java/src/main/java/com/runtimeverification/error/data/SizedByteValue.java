// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class SizedByteValue implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public SizedByteValue() {
  }

  /**
   * Construct from a JSON string.
   */
  public SizedByteValue(String s) throws JSONException {
    this(new JSONObject(s));
  }

  SizedByteValue(JSONObject jo) throws JSONException {
    length = jo.getInt("length");
    value = new ByteValue(jo.get("value"));

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (length != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"length\":");
      _out.append(String.valueOf(length));
    }
    else
      throw new JSONException("Uninitialized field length");

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

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public Integer length;
  public ByteValue value;
}
