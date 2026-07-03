// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class EnumInfo implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public EnumInfo() {
  }

  /**
   * Construct from a JSON string.
   */
  public EnumInfo(String s) throws JSONException {
    this(new JSONObject(s));
  }

  EnumInfo(JSONObject jo) throws JSONException {
    enum_name = jo.getString("enum_name");
    type_alias = new ValueType(jo.get("type_alias"));
    if (jo.has("enum_id")) {
      enum_id = jo.optString("enum_id");
    }

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (enum_name != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"enum_name\":");
      Util.writeJsonString(_out, enum_name);
    }
    else
      throw new JSONException("Uninitialized field enum_name");

    if (type_alias != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"type_alias\":");
      type_alias.toJsonBuffer(_out);
    }
    else
      throw new JSONException("Uninitialized field type_alias");

    if (enum_id != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"enum_id\":");
      Util.writeJsonString(_out, enum_id);
    }

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String enum_name;
  public ValueType type_alias;
  public String enum_id;
}
