// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

/**
 * Construct objects of type condition.
 */

public class Condition {
  Tag t = null;

  public Condition() {
  }

  public Tag tag() {
    return t;
  }

  /**
   * Define tags for sum type condition.
   */
  public enum Tag {
    CATEGORY, ERRORID, SYSTEMHEADER, LINE, FILE, SYMBOL, IFDEF, IFNDEF, DUPLICATE
  }

  public Condition(Object o) throws JSONException {
    String tag = Util.extractTag(o);
    if (tag.equals("Category")) {
      field_category = new ErrorCategory(((JSONArray)o).get(1));

      t = Tag.CATEGORY;
    }
    else if (tag.equals("ErrorId")) {
      field_errorid = ((JSONArray)o).getString(1);

      t = Tag.ERRORID;
    }
    else if (tag.equals("SystemHeader")) {
      field_systemheader = ((JSONArray)o).getBoolean(1);

      t = Tag.SYSTEMHEADER;
    }
    else if (tag.equals("Line")) {
      field_line = new LineSpec(((JSONArray)o).getJSONObject(1));

      t = Tag.LINE;
    }
    else if (tag.equals("File")) {
      field_file = ((JSONArray)o).getString(1);

      t = Tag.FILE;
    }
    else if (tag.equals("Symbol")) {
      field_symbol = ((JSONArray)o).getString(1);

      t = Tag.SYMBOL;
    }
    else if (tag.equals("Ifdef")) {
      field_ifdef = ((JSONArray)o).getString(1);

      t = Tag.IFDEF;
    }
    else if (tag.equals("Ifndef")) {
      field_ifndef = ((JSONArray)o).getString(1);

      t = Tag.IFNDEF;
    }
    else if (tag.equals("Duplicate")) {
      field_duplicate = ((JSONArray)o).getBoolean(1);

      t = Tag.DUPLICATE;
    }
    else
      throw new JSONException("Invalid tag: " + tag);
  }

  ErrorCategory field_category = null;
  public void setCategory(ErrorCategory x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.CATEGORY;
    field_category = x;
  }
  public ErrorCategory getCategory() {
    if (t == Tag.CATEGORY)
      return field_category;
    else
      return null;
  }

  String field_errorid = null;
  public void setErrorId(String x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.ERRORID;
    field_errorid = x;
  }
  public String getErrorId() {
    if (t == Tag.ERRORID)
      return field_errorid;
    else
      return null;
  }

  Boolean field_systemheader = null;
  public void setSystemHeader(Boolean x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.SYSTEMHEADER;
    field_systemheader = x;
  }
  public Boolean getSystemHeader() {
    if (t == Tag.SYSTEMHEADER)
      return field_systemheader;
    else
      return null;
  }

  LineSpec field_line = null;
  public void setLine(LineSpec x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.LINE;
    field_line = x;
  }
  public LineSpec getLine() {
    if (t == Tag.LINE)
      return field_line;
    else
      return null;
  }

  String field_file = null;
  public void setFile(String x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.FILE;
    field_file = x;
  }
  public String getFile() {
    if (t == Tag.FILE)
      return field_file;
    else
      return null;
  }

  String field_symbol = null;
  public void setSymbol(String x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.SYMBOL;
    field_symbol = x;
  }
  public String getSymbol() {
    if (t == Tag.SYMBOL)
      return field_symbol;
    else
      return null;
  }

  String field_ifdef = null;
  public void setIfdef(String x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.IFDEF;
    field_ifdef = x;
  }
  public String getIfdef() {
    if (t == Tag.IFDEF)
      return field_ifdef;
    else
      return null;
  }

  String field_ifndef = null;
  public void setIfndef(String x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.IFNDEF;
    field_ifndef = x;
  }
  public String getIfndef() {
    if (t == Tag.IFNDEF)
      return field_ifndef;
    else
      return null;
  }

  Boolean field_duplicate = null;
  public void setDuplicate(Boolean x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.DUPLICATE;
    field_duplicate = x;
  }
  public Boolean getDuplicate() {
    if (t == Tag.DUPLICATE)
      return field_duplicate;
    else
      return null;
  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    if (t == null)
      throw new JSONException("Uninitialized Condition");
    else {
      switch(t) {
      case CATEGORY:
         _out.append("[\"Category\",");
         field_category.toJsonBuffer(_out);
         _out.append("]");
         break;
      case ERRORID:
         _out.append("[\"ErrorId\",");
         Util.writeJsonString(_out, field_errorid);
         _out.append("]");
         break;
      case SYSTEMHEADER:
         _out.append("[\"SystemHeader\",");
         _out.append(String.valueOf(field_systemheader));
         _out.append("]");
         break;
      case LINE:
         _out.append("[\"Line\",");
         field_line.toJsonBuffer(_out);
         _out.append("]");
         break;
      case FILE:
         _out.append("[\"File\",");
         Util.writeJsonString(_out, field_file);
         _out.append("]");
         break;
      case SYMBOL:
         _out.append("[\"Symbol\",");
         Util.writeJsonString(_out, field_symbol);
         _out.append("]");
         break;
      case IFDEF:
         _out.append("[\"Ifdef\",");
         Util.writeJsonString(_out, field_ifdef);
         _out.append("]");
         break;
      case IFNDEF:
         _out.append("[\"Ifndef\",");
         Util.writeJsonString(_out, field_ifndef);
         _out.append("]");
         break;
      case DUPLICATE:
         _out.append("[\"Duplicate\",");
         _out.append(String.valueOf(field_duplicate));
         _out.append("]");
         break;
      default:
        break; /* unused; keeps compiler happy */
      }
    }
  }

  public String toJson() throws JSONException {
    StringBuilder out = new StringBuilder(128);
    toJsonBuffer(out);
    return out.toString();
  }
}
