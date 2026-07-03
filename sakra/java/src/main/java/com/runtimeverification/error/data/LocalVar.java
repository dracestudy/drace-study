// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class LocalVar implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public LocalVar() {
  }

  /**
   * Construct from a JSON string.
   */
  public LocalVar(String s) throws JSONException {
    this(new JSONObject(s));
  }

  LocalVar(JSONObject jo) throws JSONException {
    id = jo.getString("id");
    vtype = new ValueType(jo.get("vtype"));
    vtype_str = jo.getString("vtype_str");
    size = jo.getInt("size");
    value = new LocalVarValue(jo.get("value"));

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (id != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"id\":");
      Util.writeJsonString(_out, id);
    }
    else
      throw new JSONException("Uninitialized field id");

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

    if (vtype_str != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"vtype_str\":");
      Util.writeJsonString(_out, vtype_str);
    }
    else
      throw new JSONException("Uninitialized field vtype_str");

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
  public String id;
  public ValueType vtype;
  public String vtype_str;
  public Integer size;
  public LocalVarValue value;
}
