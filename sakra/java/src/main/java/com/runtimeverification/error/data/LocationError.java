// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class LocationError implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public LocationError() {
  }

  /**
   * Construct from a JSON string.
   */
  public LocationError(String s) throws JSONException {
    this(new JSONObject(s));
  }

  LocationError(JSONObject jo) throws JSONException {
    description = jo.getString("description");
    loc = new Frame(jo.getJSONObject("loc"));
    category = new ErrorCategory(jo.get("category"));
    error_id = jo.getString("error_id");
    citations = new java.util.ArrayList<Citation>();
    for (int _i = 0; _i < jo.getJSONArray("citations").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("citations").getJSONObject(_i);
      citations.add(new Citation(_tmp));
    }
    if (jo.has("friendly_cat")) {
      friendly_cat = jo.optString("friendly_cat");
    }
    if (jo.has("long_desc")) {
      long_desc = jo.optString("long_desc");
    }

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (description != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"description\":");
      Util.writeJsonString(_out, description);
    }
    else
      throw new JSONException("Uninitialized field description");

    if (loc != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"loc\":");
      loc.toJsonBuffer(_out);
    }
    else
      throw new JSONException("Uninitialized field loc");

    if (category != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"category\":");
      category.toJsonBuffer(_out);
    }
    else
      throw new JSONException("Uninitialized field category");

    if (error_id != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"error_id\":");
      Util.writeJsonString(_out, error_id);
    }
    else
      throw new JSONException("Uninitialized field error_id");

    if (citations != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"citations\":");
      _out.append("[");
      for (int i = 0; i < citations.size(); ++i) {
        citations.get(i).toJsonBuffer(_out);
        if (i < citations.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field citations");

    if (friendly_cat != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"friendly_cat\":");
      Util.writeJsonString(_out, friendly_cat);
    }

    if (long_desc != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"long_desc\":");
      Util.writeJsonString(_out, long_desc);
    }

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String description;
  public Frame loc;
  public ErrorCategory category;
  public String error_id;
  public java.util.ArrayList<Citation> citations;
  public String friendly_cat;
  public String long_desc;
}
