// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class RawStackTraceComponent implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public RawStackTraceComponent() {
  }

  /**
   * Construct from a JSON string.
   */
  public RawStackTraceComponent(String s) throws JSONException {
    this(new JSONObject(s));
  }

  RawStackTraceComponent(JSONObject jo) throws JSONException {
    description_format = jo.getString("description_format");
    description_fields = new java.util.ArrayList<RawComponentField>();
    for (int _i = 0; _i < jo.getJSONArray("description_fields").length(); ++_i) {
      Object _tmp = jo.getJSONArray("description_fields").get(_i);
      description_fields.add(new RawComponentField(_tmp));
    }
    frames = new java.util.ArrayList<RawFrame>();
    for (int _i = 0; _i < jo.getJSONArray("frames").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("frames").getJSONObject(_i);
      frames.add(new RawFrame(_tmp));
    }

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (description_format != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"description_format\":");
      Util.writeJsonString(_out, description_format);
    }
    else
      throw new JSONException("Uninitialized field description_format");

    if (description_fields != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"description_fields\":");
      _out.append("[");
      for (int i = 0; i < description_fields.size(); ++i) {
        description_fields.get(i).toJsonBuffer(_out);
        if (i < description_fields.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field description_fields");

    if (frames != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"frames\":");
      _out.append("[");
      for (int i = 0; i < frames.size(); ++i) {
        frames.get(i).toJsonBuffer(_out);
        if (i < frames.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field frames");

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String description_format;
  public java.util.ArrayList<RawComponentField> description_fields;
  public java.util.ArrayList<RawFrame> frames;
}
