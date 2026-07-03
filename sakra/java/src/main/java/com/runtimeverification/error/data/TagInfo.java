// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class TagInfo implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public TagInfo() {
  }

  /**
   * Construct from a JSON string.
   */
  public TagInfo(String s) throws JSONException {
    this(new JSONObject(s));
  }

  TagInfo(JSONObject jo) throws JSONException {
    tag_name = jo.getString("tag_name");
    if (jo.has("active_variant")) {
      active_variant = jo.optString("active_variant");
    }
    if (jo.has("field_info")) {
      field_info = new FieldsInfo(jo.optJSONObject("field_info"));
    }

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (tag_name != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"tag_name\":");
      Util.writeJsonString(_out, tag_name);
    }
    else
      throw new JSONException("Uninitialized field tag_name");

    if (active_variant != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"active_variant\":");
      Util.writeJsonString(_out, active_variant);
    }

    if (field_info != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"field_info\":");
      field_info.toJsonBuffer(_out);
    }

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String tag_name;
  public String active_variant;
  public FieldsInfo field_info;
}
