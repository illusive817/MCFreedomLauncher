package org.apache.commons.lang3.text;

import java.util.Iterator;
import org.apache.commons.lang3.ObjectUtils;

public class StrBuilder
  implements Appendable, CharSequence
{
  protected char[] buffer;
  protected int size;
  private String nullText;

  public StrBuilder()
  {
    this(32);
  }

  public StrBuilder(int initialCapacity)
  {
    if (initialCapacity <= 0) {
      initialCapacity = 32;
    }
    this.buffer = new char[initialCapacity];
  }

  public StrBuilder(String str)
  {
    if (str == null) {
      this.buffer = new char[32];
    } else {
      this.buffer = new char[str.length() + 32];
      append(str);
    }
  }

  public int length()
  {
    return this.size;
  }

  public StrBuilder ensureCapacity(int capacity)
  {
    if (capacity > this.buffer.length) {
      char[] old = this.buffer;
      this.buffer = new char[capacity * 2];
      System.arraycopy(old, 0, this.buffer, 0, this.size);
    }
    return this;
  }

  public char charAt(int index)
  {
    if ((index < 0) || (index >= length())) {
      throw new StringIndexOutOfBoundsException(index);
    }
    return this.buffer[index];
  }

  public StrBuilder deleteCharAt(int index)
  {
    if ((index < 0) || (index >= this.size)) {
      throw new StringIndexOutOfBoundsException(index);
    }
    deleteImpl(index, index + 1, 1);
    return this;
  }

  public StrBuilder appendNull()
  {
    if (this.nullText == null) {
      return this;
    }
    return append(this.nullText);
  }

  public StrBuilder append(Object obj)
  {
    if (obj == null) {
      return appendNull();
    }
    return append(obj.toString());
  }

  public StrBuilder append(CharSequence seq)
  {
    if (seq == null) {
      return appendNull();
    }
    return append(seq.toString());
  }

  public StrBuilder append(CharSequence seq, int startIndex, int length)
  {
    if (seq == null) {
      return appendNull();
    }
    return append(seq.toString(), startIndex, length);
  }

  public StrBuilder append(String str)
  {
    if (str == null) {
      return appendNull();
    }
    int strLen = str.length();
    if (strLen > 0) {
      int len = length();
      ensureCapacity(len + strLen);
      str.getChars(0, strLen, this.buffer, len);
      this.size += strLen;
    }
    return this;
  }

  public StrBuilder append(String str, int startIndex, int length)
  {
    if (str == null) {
      return appendNull();
    }
    if ((startIndex < 0) || (startIndex > str.length())) {
      throw new StringIndexOutOfBoundsException("startIndex must be valid");
    }
    if ((length < 0) || (startIndex + length > str.length())) {
      throw new StringIndexOutOfBoundsException("length must be valid");
    }
    if (length > 0) {
      int len = length();
      ensureCapacity(len + length);
      str.getChars(startIndex, startIndex + length, this.buffer, len);
      this.size += length;
    }
    return this;
  }

  public StrBuilder append(char ch)
  {
    int len = length();
    ensureCapacity(len + 1);
    this.buffer[(this.size++)] = ch;
    return this;
  }

  public StrBuilder appendWithSeparators(Iterable<?> iterable, String separator)
  {
    if (iterable != null) {
      separator = ObjectUtils.toString(separator);
      Iterator it = iterable.iterator();
      while (it.hasNext()) {
        append(it.next());
        if (it.hasNext()) {
          append(separator);
        }
      }
    }
    return this;
  }

  private void deleteImpl(int startIndex, int endIndex, int len)
  {
    System.arraycopy(this.buffer, endIndex, this.buffer, startIndex, this.size - endIndex);
    this.size -= len;
  }

  private void replaceImpl(int startIndex, int endIndex, int removeLen, String insertStr, int insertLen)
  {
    int newSize = this.size - removeLen + insertLen;
    if (insertLen != removeLen) {
      ensureCapacity(newSize);
      System.arraycopy(this.buffer, endIndex, this.buffer, startIndex + insertLen, this.size - endIndex);
      this.size = newSize;
    }
    if (insertLen > 0)
      insertStr.getChars(0, insertLen, this.buffer, startIndex);
  }

  public StrBuilder replace(int startIndex, int endIndex, String replaceStr)
  {
    endIndex = validateRange(startIndex, endIndex);
    int insertLen = replaceStr == null ? 0 : replaceStr.length();
    replaceImpl(startIndex, endIndex, endIndex - startIndex, replaceStr, insertLen);
    return this;
  }

  public CharSequence subSequence(int startIndex, int endIndex)
  {
    if (startIndex < 0) {
      throw new StringIndexOutOfBoundsException(startIndex);
    }
    if (endIndex > this.size) {
      throw new StringIndexOutOfBoundsException(endIndex);
    }
    if (startIndex > endIndex) {
      throw new StringIndexOutOfBoundsException(endIndex - startIndex);
    }
    return substring(startIndex, endIndex);
  }

  public String substring(int startIndex, int endIndex)
  {
    endIndex = validateRange(startIndex, endIndex);
    return new String(this.buffer, startIndex, endIndex - startIndex);
  }

  public boolean equals(StrBuilder other)
  {
    if (this == other) {
      return true;
    }
    if (this.size != other.size) {
      return false;
    }
    char[] thisBuf = this.buffer;
    char[] otherBuf = other.buffer;
    for (int i = this.size - 1; i >= 0; i--) {
      if (thisBuf[i] != otherBuf[i]) {
        return false;
      }
    }
    return true;
  }

  public boolean equals(Object obj)
  {
    if ((obj instanceof StrBuilder)) {
      return equals((StrBuilder)obj);
    }
    return false;
  }

  public int hashCode()
  {
    char[] buf = this.buffer;
    int hash = 0;
    for (int i = this.size - 1; i >= 0; i--) {
      hash = 31 * hash + buf[i];
    }
    return hash;
  }

  public String toString()
  {
    return new String(this.buffer, 0, this.size);
  }

  protected int validateRange(int startIndex, int endIndex)
  {
    if (startIndex < 0) {
      throw new StringIndexOutOfBoundsException(startIndex);
    }
    if (endIndex > this.size) {
      endIndex = this.size;
    }
    if (startIndex > endIndex) {
      throw new StringIndexOutOfBoundsException("end < start");
    }
    return endIndex;
  }
}