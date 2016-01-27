/**
 * Copyright (C) 2007 - 2016, Jens Lehmann
 *
 * This file is part of DL-Learner.
 *
 * DL-Learner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * DL-Learner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/* Generated By:JavaCC: Do not edit this line. PrologParserConstants.java */
package org.dllearner.parser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
@SuppressWarnings("all")
public interface PrologParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int SINGLE_LINE_COMMENT = 6;
  /** RegularExpression Id. */
  int NOT = 7;
  /** RegularExpression Id. */
  int DOUBLE = 8;
  /** RegularExpression Id. */
  int NUMBER = 9;
  /** RegularExpression Id. */
  int DIGIT = 10;
  /** RegularExpression Id. */
  int STRINGCONSTANT = 11;
  /** RegularExpression Id. */
  int VAR = 12;
  /** RegularExpression Id. */
  int IDENTIFIER = 13;
  /** RegularExpression Id. */
  int OPERATOR = 14;
  /** RegularExpression Id. */
  int ANYCHAR = 15;
  /** RegularExpression Id. */
  int LOCASE = 16;
  /** RegularExpression Id. */
  int HICASE = 17;
  /** RegularExpression Id. */
  int SPECIALCHAR = 18;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "<SINGLE_LINE_COMMENT>",
    "\"not\"",
    "<DOUBLE>",
    "<NUMBER>",
    "<DIGIT>",
    "<STRINGCONSTANT>",
    "<VAR>",
    "<IDENTIFIER>",
    "<OPERATOR>",
    "<ANYCHAR>",
    "<LOCASE>",
    "<HICASE>",
    "<SPECIALCHAR>",
    "\":-\"",
    "\".\"",
    "\",\"",
    "\"(\"",
    "\")\"",
    "\"[]\"",
    "\"[\"",
    "\"]\"",
    "\"|\"",
  };

}
