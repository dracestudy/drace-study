// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class Padding implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public Padding() {
  }

  /**
   * Construct from a JSON string.
   */
  public Padding(String s) throws JSONException {
    this(new JSONObject(s));
  }

  Padding(JSONObject jo) throws JSONException {
    offset = jo.getInt("offset");
    value = new SizedByteValue(jo.getJSONObject("value"));

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (offset != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"offset\":");
      _out.append(String.valueOf(offset));
    }
    else
      throw new JSONException("Uninitialized field offset");

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
  public Integer offset;
  public SizedByteValue value;
}
