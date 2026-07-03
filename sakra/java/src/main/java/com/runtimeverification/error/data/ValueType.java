// Automatically generated; do not edit
package com.runtimeverification.error.data;
import org.json.*;

/**
 * Construct objects of type value_type.
 */

public class ValueType {
  Tag t = null;

  public ValueType() {
  }

  public Tag tag() {
    return t;
  }

  /**
   * Define tags for sum type value_type.
   */
  public enum Tag {
    BOOL, BITFIELD, FLOAT_, DOUBLE_, LONGDOUBLE, VOID_, NOTYPE, SHORTINT, INT_, LONGINT, LONGLONGINT, SIGNEDCHAR, UNSIGNEDSHORTINT, UNSIGNEDINT, UNSIGNEDLONGINT, UNSIGNEDLONGLONGINT, UNSIGNEDCHAR, CHAR_, POINTER, ENUM_, UNION, STRUCT, ARRAY, INCOMPLETEARRAY, VARIABLEARRAY, FUNCTION
  }

  public ValueType(Object o) throws JSONException {
    String tag = Util.extractTag(o);
    if (tag.equals("Bool"))
      t = Tag.BOOL;
    else if (tag.equals("Bitfield")) {
      field_bitfield = new SizedType(((JSONArray)o).getJSONObject(1));

      t = Tag.BITFIELD;
    }
    else if (tag.equals("Float"))
      t = Tag.FLOAT_;
    else if (tag.equals("Double"))
      t = Tag.DOUBLE_;
    else if (tag.equals("LongDouble"))
      t = Tag.LONGDOUBLE;
    else if (tag.equals("Void"))
      t = Tag.VOID_;
    else if (tag.equals("NoType"))
      t = Tag.NOTYPE;
    else if (tag.equals("ShortInt"))
      t = Tag.SHORTINT;
    else if (tag.equals("Int"))
      t = Tag.INT_;
    else if (tag.equals("LongInt"))
      t = Tag.LONGINT;
    else if (tag.equals("LongLongInt"))
      t = Tag.LONGLONGINT;
    else if (tag.equals("SignedChar"))
      t = Tag.SIGNEDCHAR;
    else if (tag.equals("UnsignedShortInt"))
      t = Tag.UNSIGNEDSHORTINT;
    else if (tag.equals("UnsignedInt"))
      t = Tag.UNSIGNEDINT;
    else if (tag.equals("UnsignedLongInt"))
      t = Tag.UNSIGNEDLONGINT;
    else if (tag.equals("UnsignedLongLongInt"))
      t = Tag.UNSIGNEDLONGLONGINT;
    else if (tag.equals("UnsignedChar"))
      t = Tag.UNSIGNEDCHAR;
    else if (tag.equals("Char"))
      t = Tag.CHAR_;
    else if (tag.equals("Pointer")) {
      field_pointer = new ValueType(((JSONArray)o).get(1));

      t = Tag.POINTER;
    }
    else if (tag.equals("Enum")) {
      field_enum_ = new EnumInfo(((JSONArray)o).getJSONObject(1));

      t = Tag.ENUM_;
    }
    else if (tag.equals("Union")) {
      field_union = new TagInfo(((JSONArray)o).getJSONObject(1));

      t = Tag.UNION;
    }
    else if (tag.equals("Struct")) {
      field_struct = new TagInfo(((JSONArray)o).getJSONObject(1));

      t = Tag.STRUCT;
    }
    else if (tag.equals("Array")) {
      field_array = new ArrayType(((JSONArray)o).getJSONObject(1));

      t = Tag.ARRAY;
    }
    else if (tag.equals("IncompleteArray")) {
      field_incompletearray = new ArrayType(((JSONArray)o).getJSONObject(1));

      t = Tag.INCOMPLETEARRAY;
    }
    else if (tag.equals("VariableArray")) {
      field_variablearray = new ArrayType(((JSONArray)o).getJSONObject(1));

      t = Tag.VARIABLEARRAY;
    }
    else if (tag.equals("Function"))
      t = Tag.FUNCTION;
    else
      throw new JSONException("Invalid tag: " + tag);
  }

  public void setBool() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.BOOL;
  }

  SizedType field_bitfield = null;
  public void setBitfield(SizedType x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.BITFIELD;
    field_bitfield = x;
  }
  public SizedType getBitfield() {
    if (t == Tag.BITFIELD)
      return field_bitfield;
    else
      return null;
  }

  public void setFloat() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.FLOAT_;
  }

  public void setDouble() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.DOUBLE_;
  }

  public void setLongDouble() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.LONGDOUBLE;
  }

  public void setVoid() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.VOID_;
  }

  public void setNoType() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.NOTYPE;
  }

  public void setShortInt() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.SHORTINT;
  }

  public void setInt() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.INT_;
  }

  public void setLongInt() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.LONGINT;
  }

  public void setLongLongInt() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.LONGLONGINT;
  }

  public void setSignedChar() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.SIGNEDCHAR;
  }

  public void setUnsignedShortInt() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.UNSIGNEDSHORTINT;
  }

  public void setUnsignedInt() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.UNSIGNEDINT;
  }

  public void setUnsignedLongInt() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.UNSIGNEDLONGINT;
  }

  public void setUnsignedLongLongInt() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.UNSIGNEDLONGLONGINT;
  }

  public void setUnsignedChar() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.UNSIGNEDCHAR;
  }

  public void setChar() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.CHAR_;
  }

  ValueType field_pointer = null;
  public void setPointer(ValueType x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.POINTER;
    field_pointer = x;
  }
  public ValueType getPointer() {
    if (t == Tag.POINTER)
      return field_pointer;
    else
      return null;
  }

  EnumInfo field_enum_ = null;
  public void setEnum(EnumInfo x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.ENUM_;
    field_enum_ = x;
  }
  public EnumInfo getEnum() {
    if (t == Tag.ENUM_)
      return field_enum_;
    else
      return null;
  }

  TagInfo field_union = null;
  public void setUnion(TagInfo x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.UNION;
    field_union = x;
  }
  public TagInfo getUnion() {
    if (t == Tag.UNION)
      return field_union;
    else
      return null;
  }

  TagInfo field_struct = null;
  public void setStruct(TagInfo x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.STRUCT;
    field_struct = x;
  }
  public TagInfo getStruct() {
    if (t == Tag.STRUCT)
      return field_struct;
    else
      return null;
  }

  ArrayType field_array = null;
  public void setArray(ArrayType x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.ARRAY;
    field_array = x;
  }
  public ArrayType getArray() {
    if (t == Tag.ARRAY)
      return field_array;
    else
      return null;
  }

  ArrayType field_incompletearray = null;
  public void setIncompleteArray(ArrayType x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.INCOMPLETEARRAY;
    field_incompletearray = x;
  }
  public ArrayType getIncompleteArray() {
    if (t == Tag.INCOMPLETEARRAY)
      return field_incompletearray;
    else
      return null;
  }

  ArrayType field_variablearray = null;
  public void setVariableArray(ArrayType x) {
    /* TODO: clear previously-set field in order to avoid memory leak */
    t = Tag.VARIABLEARRAY;
    field_variablearray = x;
  }
  public ArrayType getVariableArray() {
    if (t == Tag.VARIABLEARRAY)
      return field_variablearray;
    else
      return null;
  }

  public void setFunction() {
    /* TODO: clear previously-set field and avoid memory leak */
    t = Tag.FUNCTION;
  }

  public void toJsonBuffer(StringBuilder _out) throws JSONException {
    if (t == null)
      throw new JSONException("Uninitialized ValueType");
    else {
      switch(t) {
      case BOOL:
        _out.append("\"Bool\"");
        break;
      case BITFIELD:
         _out.append("[\"Bitfield\",");
         field_bitfield.toJsonBuffer(_out);
         _out.append("]");
         break;
      case FLOAT_:
        _out.append("\"Float\"");
        break;
      case DOUBLE_:
        _out.append("\"Double\"");
        break;
      case LONGDOUBLE:
        _out.append("\"LongDouble\"");
        break;
      case VOID_:
        _out.append("\"Void\"");
        break;
      case NOTYPE:
        _out.append("\"NoType\"");
        break;
      case SHORTINT:
        _out.append("\"ShortInt\"");
        break;
      case INT_:
        _out.append("\"Int\"");
        break;
      case LONGINT:
        _out.append("\"LongInt\"");
        break;
      case LONGLONGINT:
        _out.append("\"LongLongInt\"");
        break;
      case SIGNEDCHAR:
        _out.append("\"SignedChar\"");
        break;
      case UNSIGNEDSHORTINT:
        _out.append("\"UnsignedShortInt\"");
        break;
      case UNSIGNEDINT:
        _out.append("\"UnsignedInt\"");
        break;
      case UNSIGNEDLONGINT:
        _out.append("\"UnsignedLongInt\"");
        break;
      case UNSIGNEDLONGLONGINT:
        _out.append("\"UnsignedLongLongInt\"");
        break;
      case UNSIGNEDCHAR:
        _out.append("\"UnsignedChar\"");
        break;
      case CHAR_:
        _out.append("\"Char\"");
        break;
      case POINTER:
         _out.append("[\"Pointer\",");
         field_pointer.toJsonBuffer(_out);
         _out.append("]");
         break;
      case ENUM_:
         _out.append("[\"Enum\",");
         field_enum_.toJsonBuffer(_out);
         _out.append("]");
         break;
      case UNION:
         _out.append("[\"Union\",");
         field_union.toJsonBuffer(_out);
         _out.append("]");
         break;
      case STRUCT:
         _out.append("[\"Struct\",");
         field_struct.toJsonBuffer(_out);
         _out.append("]");
         break;
      case ARRAY:
         _out.append("[\"Array\",");
         field_array.toJsonBuffer(_out);
         _out.append("]");
         break;
      case INCOMPLETEARRAY:
         _out.append("[\"IncompleteArray\",");
         field_incompletearray.toJsonBuffer(_out);
         _out.append("]");
         break;
      case VARIABLEARRAY:
         _out.append("[\"VariableArray\",");
         field_variablearray.toJsonBuffer(_out);
         _out.append("]");
         break;
      case FUNCTION:
        _out.append("\"Function\"");
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
