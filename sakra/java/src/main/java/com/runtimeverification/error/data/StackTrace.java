// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class StackTrace implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public StackTrace() {
  }

  /**
   * Construct from a JSON string.
   */
  public StackTrace(String s) throws JSONException {
    this(new JSONObject(s));
  }

  StackTrace(JSONObject jo) throws JSONException {
    components = new java.util.ArrayList<StackTraceComponent>();
    for (int _i = 0; _i < jo.getJSONArray("components").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("components").getJSONObject(_i);
      components.add(new StackTraceComponent(_tmp));
    }
    if (jo.has("thread_id")) {
      thread_id = jo.optString("thread_id");
    }
    if (jo.has("thread_created_by")) {
      thread_created_by = jo.optString("thread_created_by");
    }
    if (jo.has("thread_created_at")) {
      thread_created_at = new Frame(jo.optJSONObject("thread_created_at"));
    }

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (components != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"components\":");
      _out.append("[");
      for (int i = 0; i < components.size(); ++i) {
        components.get(i).toJsonBuffer(_out);
        if (i < components.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field components");

    if (thread_id != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"thread_id\":");
      Util.writeJsonString(_out, thread_id);
    }

    if (thread_created_by != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"thread_created_by\":");
      Util.writeJsonString(_out, thread_created_by);
    }

    if (thread_created_at != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"thread_created_at\":");
      thread_created_at.toJsonBuffer(_out);
    }

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public java.util.ArrayList<StackTraceComponent> components;
  public String thread_id;
  public String thread_created_by;
  public Frame thread_created_at;
}
