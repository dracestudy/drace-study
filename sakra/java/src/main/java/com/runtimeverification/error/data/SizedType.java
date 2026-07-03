// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class SizedType implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public SizedType() {
  }

  /**
   * Construct from a JSON string.
   */
  public SizedType(String s) throws JSONException {
    this(new JSONObject(s));
  }

  SizedType(JSONObject jo) throws JSONException {
    vtype = new ValueType(jo.get("vtype"));
    size = jo.getInt("size");

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (vtype != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"vtype\":");
      vtype.toJsonBuffer(_out);
    }
    else
      throw new JSONException("Uninitialized field vtype");

    if (size != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"size\":");
      _out.append(String.valueOf(size));
    }
    else
      throw new JSONException("Uninitialized field size");

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public ValueType vtype;
  public Integer size;
}
