// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class Location implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public Location() {
  }

  /**
   * Construct from a JSON string.
   */
  public Location(String s) throws JSONException {
    this(new JSONObject(s));
  }

  Location(JSONObject jo) throws JSONException {
    rel_file = jo.getString("rel_file");
    abs_file = jo.getString("abs_file");
    if (jo.has("line")) {
      line = jo.optInt("line");
    }
    if (jo.has("column")) {
      column = jo.optInt("column");
    }
    system_header = jo.getBoolean("system_header");

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (rel_file != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"rel_file\":");
      Util.writeJsonString(_out, rel_file);
    }
    else
      throw new JSONException("Uninitialized field rel_file");

    if (abs_file != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"abs_file\":");
      Util.writeJsonString(_out, abs_file);
    }
    else
      throw new JSONException("Uninitialized field abs_file");

    if (line != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"line\":");
      _out.append(String.valueOf(line));
    }

    if (column != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"column\":");
      _out.append(String.valueOf(column));
    }

    if (system_header != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"system_header\":");
      _out.append(String.valueOf(system_header));
    }
    else
      throw new JSONException("Uninitialized field system_header");

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String rel_file;
  public String abs_file;
  public Integer line;
  public Integer column;
  public Boolean system_header;
}
