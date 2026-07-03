// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

/**
 * Construct objects of type error_category.
 */

public class ErrorCategory {
  Tag t = null;

  public ErrorCategory() {
  }

  public Tag tag() {
    return t;
  }

  /**
   * Define tags for sum type error_category.
   */
  public enum Tag {
    UNDEFINED, UNSPECIFIED, IMPLEMENTATIONDEFINED, IMPLEMENTATIONUNDEFINED, CONDITIONALLYSUPPORTED, ILLFORMED, UNDERSPECIFIED, CONSTRAINTVIOLATION, SYNTAXERROR, LINTERROR, UNKNOWN
  }

  public ErrorCategory(Object o) throws JSONException {
    String tag = Util.extractTag(o);
    if (tag.equals("Undefined")) {
      field_undefined = new Language(((JSONArray)o).get(1));

      t = Tag.UNDEFINED;
    }
    else if (tag.equals("Unspecified")) {
      field_unspecified = new Language(((JSONArray)o).get(1));

      t = Tag.UNSPECIFIED;
    }
    else if (tag.equals("ImplementationDefined")) {
      field_implementationdefined = new Language(((JSONArray)o).get(1));

      t = Tag.IMPLEMENTATIONDEFINED;
    }
    else if (tag.equals("ImplementationUndefined")) {
      field_implementationundefined = new Language(((JSONArray)o).get(1));

      t = Tag.IMPLEMENTATIONUNDEFINED;
    }
    else if (tag.equals("ConditionallySupported"))
      t = Tag.CONDITIONALLYSUPPORTED;
    else if (tag.equals("IllFormed"))
      t = Tag.ILLFORMED;
    else if (tag.equals("Underspecified")) {
      field_underspecified = new Language(((JSONArray)o).get(1));

      t = Tag.UNDERSPECIFIED;
    }
    else if (tag.equals("ConstraintViolation"))
      t = Tag.CONSTRAINTVIOLATION;
    else if (tag.equals("SyntaxError")) {
      field_syntaxerror = new Language(((JSONArray)o).get(1));

      t = Tag.SYNTAXERROR;
    }
    else if (tag.equals("LintError"))
      t = Tag.LINTERROR;
    else if (tag.equals("Unknown"))
      t = Tag.UNKNOWN;
    else
      throw new JSONException("Invalid tag: " + tag);
  }

  Language field_undefined = null;
  public void setUndefined(Language x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.UNDEFINED;
    field_undefined = x;
  }
  public Language getUndefined() {
    if (t == Tag.UNDEFINED)
      return field_undefined;
    else
      return null;
  }

  Language field_unspecified = null;
  public void setUnspecified(Language x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.UNSPECIFIED;
    field_unspecified = x;
  }
  public Language getUnspecified() {
    if (t == Tag.UNSPECIFIED)
      return field_unspecified;
    else
      return null;
  }

  Language field_implementationdefined = null;
  public void setImplementationDefined(Language x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.IMPLEMENTATIONDEFINED;
    field_implementationdefined = x;
  }
  public Language getImplementationDefined() {
    if (t == Tag.IMPLEMENTATIONDEFINED)
      return field_implementationdefined;
    else
      return null;
  }

  Language field_implementationundefined = null;
  public void setImplementationUndefined(Language x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.IMPLEMENTATIONUNDEFINED;
    field_implementationundefined = x;
  }
  public Language getImplementationUndefined() {
    if (t == Tag.IMPLEMENTATIONUNDEFINED)
      return field_implementationundefined;
    else
      return null;
  }

  public void setConditionallySupported() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.CONDITIONALLYSUPPORTED;
  }

  public void setIllFormed() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.ILLFORMED;
  }

  Language field_underspecified = null;
  public void setUnderspecified(Language x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.UNDERSPECIFIED;
    field_underspecified = x;
  }
  public Language getUnderspecified() {
    if (t == Tag.UNDERSPECIFIED)
      return field_underspecified;
    else
      return null;
  }

  public void setConstraintViolation() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.CONSTRAINTVIOLATION;
  }

  Language field_syntaxerror = null;
  public void setSyntaxError(Language x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.SYNTAXERROR;
    field_syntaxerror = x;
  }
  public Language getSyntaxError() {
    if (t == Tag.SYNTAXERROR)
      return field_syntaxerror;
    else
      return null;
  }

  public void setLintError() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.LINTERROR;
  }

  public void setUnknown() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.UNKNOWN;
  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    if (t == null)
      throw new JSONException("Uninitialized ErrorCategory");
    else {
      switch(t) {
      case UNDEFINED:
         _out.append("[\"Undefined\",");
         field_undefined.toJsonBuffer(_out);
         _out.append("]");
         break;
      case UNSPECIFIED:
         _out.append("[\"Unspecified\",");
         field_unspecified.toJsonBuffer(_out);
         _out.append("]");
         break;
      case IMPLEMENTATIONDEFINED:
         _out.append("[\"ImplementationDefined\",");
         field_implementationdefined.toJsonBuffer(_out);
         _out.append("]");
         break;
      case IMPLEMENTATIONUNDEFINED:
         _out.append("[\"ImplementationUndefined\",");
         field_implementationundefined.toJsonBuffer(_out);
         _out.append("]");
         break;
      case CONDITIONALLYSUPPORTED:
        _out.append("\"ConditionallySupported\"");
        break;
      case ILLFORMED:
        _out.append("\"IllFormed\"");
        break;
      case UNDERSPECIFIED:
         _out.append("[\"Underspecified\",");
         field_underspecified.toJsonBuffer(_out);
         _out.append("]");
         break;
      case CONSTRAINTVIOLATION:
        _out.append("\"ConstraintViolation\"");
        break;
      case SYNTAXERROR:
         _out.append("[\"SyntaxError\",");
         field_syntaxerror.toJsonBuffer(_out);
         _out.append("]");
         break;
      case LINTERROR:
        _out.append("\"LintError\"");
        break;
      case UNKNOWN:
        _out.append("\"Unknown\"");
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
