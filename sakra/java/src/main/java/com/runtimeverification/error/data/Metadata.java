// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

public class Metadata implements Atdj {
  /**
   * Construct from a fresh record with null fields.
   */
  public Metadata() {
  }

  /**
   * Construct from a JSON string.
   */
  public Metadata(String s) throws JSONException {
    this(new JSONObject(s));
  }

  Metadata(JSONObject jo) throws JSONException {
    suppressions = new java.util.ArrayList<Suppression>();
    for (int _i = 0; _i < jo.getJSONArray("suppressions").length(); ++_i) {
      JSONObject _tmp = jo.getJSONArray("suppressions").getJSONObject(_i);
      suppressions.add(new Suppression(_tmp));
    }
    message_length = jo.getInt("message_length");
    format = new Format(jo.get("format"));
    if (jo.has("output")) {
      output = jo.optString("output");
    }
    previous_errors = new java.util.ArrayList<String>();
    for (int _i = 0; _i < jo.getJSONArray("previous_errors").length(); ++_i) {
      String _tmp = jo.getJSONArray("previous_errors").getString(_i);
      previous_errors.add(_tmp);
    }
    fatal_errors = jo.getBoolean("fatal_errors");
    rv_error = jo.getString("rv_error");

  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    boolean _isFirst = true;
    _out.append("{");
    if (suppressions != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"suppressions\":");
      _out.append("[");
      for (int i = 0; i < suppressions.size(); ++i) {
        suppressions.get(i).toJsonBuffer(_out);
        if (i < suppressions.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field suppressions");

    if (message_length != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"message_length\":");
      _out.append(String.valueOf(message_length));
    }
    else
      throw new JSONException("Uninitialized field message_length");

    if (format != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"format\":");
      format.toJsonBuffer(_out);
    }
    else
      throw new JSONException("Uninitialized field format");

    if (output != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"output\":");
      Util.writeJsonString(_out, output);
    }

    if (previous_errors != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"previous_errors\":");
      _out.append("[");
      for (int i = 0; i < previous_errors.size(); ++i) {
        Util.writeJsonString(_out, previous_errors.get(i));
        if (i < previous_errors.size() - 1)
          _out.append(",");
      }
      _out.append("]");
    }
    else
      throw new JSONException("Uninitialized field previous_errors");

    if (fatal_errors != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"fatal_errors\":");
      _out.append(String.valueOf(fatal_errors));
    }
    else
      throw new JSONException("Uninitialized field fatal_errors");

    if (rv_error != null) {
      if (_isFirst)
        _isFirst = false;
      else
        _out.append(",");
      _out.append("\"rv_error\":");
      Util.writeJsonString(_out, rv_error);
    }
    else
      throw new JSONException("Uninitialized field rv_error");

    _out.append("}");
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
  public java.util.ArrayList<Suppression> suppressions;
  public Integer message_length;
  public Format format;
  public String output;
  public java.util.ArrayList<String> previous_errors;
  public Boolean fatal_errors;
  public String rv_error;
}
