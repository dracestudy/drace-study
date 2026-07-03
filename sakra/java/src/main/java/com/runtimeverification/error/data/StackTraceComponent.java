// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class StackTraceComponent implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public StackTraceComponent() {
  }

  /**
   * Construct from a JSON string.
   */
  public StackTraceComponent(String s) throws JSONException {
    this(new JSONObject(s));
  }

  StackTraceComponent(JSONObject jo) throws JSONException {
    if (jo.has("description")) {
      description = jo.optString("description");
    }
    frames = new java.util.ArrayList<Frame>();
    for (int _i = 0; _i < jo.getJSONArray("frames").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("frames").getJSONObject(_i);
      frames.add(new Frame(_tmp));
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
  public String description;
  public java.util.ArrayList<Frame> frames;
}
