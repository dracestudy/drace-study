// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class LineSpec implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public LineSpec() {
  }

  /**
   * Construct from a JSON string.
   */
  public LineSpec(String s) throws JSONException {
    this(new JSONObject(s));
  }

  LineSpec(JSONObject jo) throws JSONException {
    start_line = jo.getInt("start_line");
    end_line = jo.getInt("end_line");
    if (jo.has("file")) {
      file = jo.optString("file");
    }

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (start_line != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"start_line\":");
      _out.append(String.valueOf(start_line));
    }
    else
      throw new JSONException("Uninitialized field start_line");

    if (end_line != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"end_line\":");
      _out.append(String.valueOf(end_line));
    }
    else
      throw new JSONException("Uninitialized field end_line");

    if (file != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"file\":");
      Util.writeJsonString(_out, file);
    }

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public Integer start_line;
  public Integer end_line;
  public String file;
}
