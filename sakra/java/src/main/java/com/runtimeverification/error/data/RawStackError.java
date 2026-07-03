// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class RawStackError implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public RawStackError() {
  }

  /**
   * Construct from a JSON string.
   */
  public RawStackError(String s) throws JSONException {
    this(new JSONObject(s));
  }

  RawStackError(JSONObject jo) throws JSONException {
    description_format = jo.getString("description_format");
    description_fields = new java.util.ArrayList<RawField>();
    for (int _i = 0; _i < jo.getJSONArray("description_fields").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("description_fields").getJSONObject(_i);
      description_fields.add(new RawField(_tmp));
    }
    stack_traces = new java.util.ArrayList<RawStackTrace>();
    for (int _i = 0; _i < jo.getJSONArray("stack_traces").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("stack_traces").getJSONObject(_i);
      stack_traces.add(new RawStackTrace(_tmp));
    }
    category = new ErrorCategory(jo.get("category"));
    error_id = jo.getString("error_id");

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

    if (stack_traces != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"stack_traces\":");
      _out.append("[");
      for (int i = 0; i < stack_traces.size(); ++i) {
        stack_traces.get(i).toJsonBuffer(_out);
        if (i < stack_traces.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field stack_traces");

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

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public String description_format;
  public java.util.ArrayList<RawField> description_fields;
  public java.util.ArrayList<RawStackTrace> stack_traces;
  public ErrorCategory category;
  public String error_id;
}
