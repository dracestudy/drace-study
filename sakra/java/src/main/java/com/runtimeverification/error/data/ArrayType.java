// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class ArrayType implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public ArrayType() {
  }

  /**
   * Construct from a JSON string.
   */
  public ArrayType(String s) throws JSONException {
    this(new JSONObject(s));
  }

  ArrayType(JSONObject jo) throws JSONException {
    vtype = new ValueType(jo.get("vtype"));
    element_bitsize = jo.getInt("element_bitsize");
    if (jo.has("length")) {
      length = jo.optInt("length");
    }

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

    if (element_bitsize != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"element_bitsize\":");
      _out.append(String.valueOf(element_bitsize));
    }
    else
      throw new JSONException("Uninitialized field element_bitsize");

    if (length != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"length\":");
      _out.append(String.valueOf(length));
    }

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public ValueType vtype;
  public Integer element_bitsize;
  public Integer length;
}
