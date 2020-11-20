/*
 *  The scanner definition for COOL.
 */
import java_cup.runtime.Symbol;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  
*/
    // Max size of string constants
    static int MAX_STR_CONST = 1025;
    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();
    /* Takes a String or a Char, appends to buffer, and checks length */
    private boolean appendAndCheckLength(Object s) {
	string_buf.append(s);
	if (string_buf.length() >= MAX_STR_CONST) { return false; }
	return true;
    }
    private int curr_lineno = 1;
    int get_curr_lineno() {
	return curr_lineno;
    }
    private AbstractSymbol filename;
    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }
    AbstractSymbol curr_filename() {
	return filename;
    }
    private int nestedCommentCount = 0;
    private char getASCII(char c) {
        switch(c) {
            case 'n':
                return '\n';
            case 't':
                return '\t';
            case 'f':
                return '\f';
            case 'b':
                return '\b';
            default:
                return c;
        }
    }
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. 
*/
    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int STRING = 1;
	private final int STRING_ERROR = 4;
	private final int BLOCK_COMMENT = 3;
	private final int YYINITIAL = 0;
	private final int COMMENT = 2;
	private final int yy_state_dtrans[] = {
		0,
		50,
		90,
		58,
		95
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NOT_ACCEPT,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NOT_ACCEPT,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NOT_ACCEPT,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR,
		/* 176 */ YY_NO_ANCHOR,
		/* 177 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"3,6:7,44:2,2,44:2,7,6:18,44,6,1,6:4,4,28,30,29,35,33,27,36,34,62:10,38,31,3" +
"2,37,43,6,42,46,47,48,49,50,18,47,51,52,47:2,53,47,54,55,56,47,57,58,19,59," +
"22,60,47:3,6,5,6:2,63,6,13,61,16,24,11,12,61,20,17,61:2,14,61,21,23,25,61,9" +
",15,8,10,45,26,61:3,39,6,40,41,6,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,178,
"0,1:5,2,3,4,5,6,1:2,7,1:4,8,1:5,9,10:2,11,10,1:6,10:15,12,1:7,13,1,14,1:4,1" +
"5,16,10,17,18,19:2,20,19:14,21,22,23,24,25,26,27,1,28,29,30,31,32,33,34,35," +
"36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60," +
"61,62,63,64,65,66,10,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84," +
"85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,10,19,100,101,102,103,104,105," +
"106,107")[0];

	private int yy_nxt[][] = unpackFromString(108,64,
"1,2,3,4:4,5,6,168:2,170,68,168,126,168,172,91,7,69,168,128,169,96,168,174,1" +
"76,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,4,5,67,169:2,171,169,173,1" +
"69,92,127,129,97,175,169:3,177,168,24,4,-1:72,168,130,168:10,132,168:6,-1:1" +
"8,168:6,132,168:5,130,168:4,134:2,-1:8,169:9,70,169:9,-1:18,169:7,70,169:11" +
",-1:27,29,-1:65,30,-1:64,31,-1:60,32,-1:9,33,-1:69,34,-1:82,24,-1:9,168:19," +
"-1:18,168:17,134:2,-1:8,168:12,160,168:6,-1:18,168:6,160,168:10,134:2,1,51," +
"52,53,87,66,93:58,1,88,59,88:25,60,89,94,88:33,-1:29,61,-1:38,53,-1:60,54,5" +
"5,54:4,-1,54:56,-1:8,168:5,140,168:3,25,168:9,-1:18,168,140,168:5,25,168:9," +
"134:2,-1:8,169:12,131,169:6,-1:18,169:6,131,169:12,-1:8,169:19,-1:18,169:19" +
",-1:8,169:12,153,169:6,-1:18,169:6,153,169:12,-1:3,65,93,-1,93:58,-1,88,-1," +
"88:25,-1:3,88:33,-1:30,62,-1:33,1,56,57,56:4,-1,56:56,-1:8,168:4,26,168:2,1" +
"48,168:2,26,168:2,27,168:5,-1:18,168:9,27,168:3,148,168:3,134:2,-1:8,169:4," +
"71,169:2,141,169:2,71,169:2,72,169:5,-1:18,169:9,72,169:3,141,169:5,-1:4,93" +
",-1,93:58,1,63:2,64:61,-1:8,168:4,28,168:5,28,168:8,-1:18,168:17,134:2,-1:8" +
",169:4,73,169:5,73,169:8,-1:18,169:19,-1:8,35,168:10,35,168:7,-1:18,168:17," +
"134:2,-1:8,74,169:10,74,169:7,-1:18,169:19,-1:8,168:18,36,-1:18,168:15,36,1" +
"68,134:2,-1:8,169:18,75,-1:18,169:15,75,169:3,-1:8,37,168:10,37,168:7,-1:18" +
",168:17,134:2,-1:8,76,169:10,76,169:7,-1:18,169:19,-1:8,168:3,38,168:15,-1:" +
"18,168:5,38,168:11,134:2,-1:8,169:13,77,169:5,-1:18,169:9,77,169:9,-1:8,168" +
":13,39,168:5,-1:18,168:9,39,168:7,134:2,-1:8,169:3,81,169:15,-1:18,169:5,81" +
",169:13,-1:8,168:3,40,168:15,-1:18,168:5,40,168:11,134:2,-1:8,169:3,78,169:" +
"15,-1:18,169:5,78,169:13,-1:8,168:8,41,168:10,-1:18,168:3,41,168:13,134:2,-" +
"1:8,169:8,79,169:10,-1:18,169:3,79,169:15,-1:8,168:17,42,168,-1:18,168:11,4" +
"2,168:5,134:2,-1:8,169:17,80,169,-1:18,169:11,80,169:7,-1:8,168:3,43,168:15" +
",-1:18,168:5,43,168:11,134:2,-1:8,169:6,82,169:12,-1:18,169:8,82,169:10,-1:" +
"8,168:6,44,168:12,-1:18,168:8,44,168:8,134:2,-1:8,169:7,83,169:11,-1:18,169" +
":13,83,169:5,-1:8,168:3,45,168:15,-1:18,168:5,45,168:11,134:2,-1:8,169:3,84" +
",169:15,-1:18,169:5,84,169:13,-1:8,168:7,46,168:11,-1:18,168:13,46,168:3,13" +
"4:2,-1:8,169:16,85,169:2,-1:18,169:4,85,169:14,-1:8,168:3,47,168:15,-1:18,1" +
"68:5,47,168:11,134:2,-1:8,169:7,86,169:11,-1:18,169:13,86,169:5,-1:8,168:16" +
",48,168:2,-1:18,168:4,48,168:12,134:2,-1:8,168:7,49,168:11,-1:18,168:13,49," +
"168:3,134:2,-1:8,168:3,98,168:11,142,168:3,-1:18,168:5,98,168:4,142,168:6,1" +
"34:2,-1:8,169:3,99,169:11,143,169:3,-1:18,169:5,99,169:4,143,169:8,-1:8,168" +
":3,100,168:11,102,168:3,-1:18,168:5,100,168:4,102,168:6,134:2,-1:8,169:3,10" +
"1,169:11,103,169:3,-1:18,169:5,101,169:4,103,169:8,-1:8,168:2,104,168:16,-1" +
":18,168:14,104,168:2,134:2,-1:8,169:3,105,169:15,-1:18,169:5,105,169:13,-1:" +
"8,168:3,106,168:15,-1:18,168:5,106,168:11,134:2,-1:8,169:7,107,169:11,-1:18" +
",169:13,107,169:5,-1:8,169:5,149,169:13,-1:18,169,149,169:17,-1:8,168:7,108" +
",168:11,-1:18,168:13,108,168:3,134:2,-1:8,169:7,109,169:11,-1:18,169:13,109" +
",169:5,-1:8,168:5,110,168:13,-1:18,168,110,168:15,134:2,-1:8,169:5,111,169:" +
"13,-1:18,169,111,169:17,-1:8,168:6,154,168:12,-1:18,168:8,154,168:8,134:2,-" +
"1:8,169:14,151,169:4,-1:18,151,169:18,-1:8,168:15,112,168:3,-1:18,168:10,11" +
"2,168:6,134:2,-1:8,169:15,113,169:3,-1:18,169:10,113,169:8,-1:8,168:7,114,1" +
"68:11,-1:18,168:13,114,168:3,134:2,-1:8,169:15,115,169:3,-1:18,169:10,115,1" +
"69:8,-1:8,168:5,156,168:13,-1:18,168,156,168:15,134:2,-1:8,169:9,155,169:9," +
"-1:18,169:7,155,169:11,-1:8,168:14,158,168:4,-1:18,158,168:16,134:2,-1:8,16" +
"9:7,117,169:11,-1:18,169:13,117,169:5,-1:8,168:15,116,168:3,-1:18,168:10,11" +
"6,168:6,134:2,-1:8,169:15,157,169:3,-1:18,169:10,157,169:8,-1:8,168:9,162,1" +
"68:9,-1:18,168:7,162,168:9,134:2,-1:8,169:3,159,169:15,-1:18,169:5,159,169:" +
"13,-1:8,168:7,118,168:11,-1:18,168:13,118,168:3,134:2,-1:8,169:6,119,169:12" +
",-1:18,169:8,119,169:10,-1:8,168:7,120,168:11,-1:18,168:13,120,168:3,134:2," +
"-1:8,169:9,121,169:9,-1:18,169:7,121,169:11,-1:8,168:15,164,168:3,-1:18,168" +
":10,164,168:6,134:2,-1:8,169,161,169:17,-1:18,169:12,161,169:6,-1:8,168:3,1" +
"65,168:15,-1:18,168:5,165,168:11,134:2,-1:8,169:9,163,169:9,-1:18,169:7,163" +
",169:11,-1:8,168:6,122,168:12,-1:18,168:8,122,168:8,134:2,-1:8,123,169:10,1" +
"23,169:7,-1:18,169:19,-1:8,168:9,124,168:9,-1:18,168:7,124,168:9,134:2,-1:8" +
",168,166,168:17,-1:18,168:12,166,168:4,134:2,-1:8,168:9,167,168:9,-1:18,168" +
":7,167,168:9,134:2,-1:8,125,168:10,125,168:7,-1:18,168:17,134:2,-1:8,168:6," +
"136,138,168:11,-1:18,168:8,136,168:4,138,168:3,134:2,-1:8,169:5,133,135,169" +
":12,-1:18,169,133,169:6,135,169:10,-1:8,168:5,144,146,168:12,-1:18,168,144," +
"168:6,146,168:8,134:2,-1:8,169:6,137,139,169:11,-1:18,169:8,137,169:4,139,1" +
"69:5,-1:8,168:15,150,168:3,-1:18,168:10,150,168:6,134:2,-1:8,169:15,145,169" +
":3,-1:18,169:10,145,169:8,-1:8,168:12,152,168:6,-1:18,168:6,152,168:10,134:" +
"2,-1:8,169:12,147,169:6,-1:18,169:6,147,169:12");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  
*/
    switch(yy_lexical_state) {
    case YYINITIAL:
	break;
    case STRING:
	yybegin(YYINITIAL);
	return new Symbol(TokenConstants.ERROR, "EOF in string constant");	
    case STRING_ERROR:
    case BLOCK_COMMENT:
	yybegin(YYINITIAL);
	return new Symbol(TokenConstants.ERROR, "EOF in comment");
    }
    return new Symbol(TokenConstants.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ string_buf.setLength(0); yybegin(STRING); }
					case -3:
						break;
					case 3:
						{ curr_lineno++; }
					case -4:
						break;
					case 4:
						{ return new Symbol(TokenConstants.ERROR, yytext()); }
					case -5:
						break;
					case 5:
						{ }
					case -6:
						break;
					case 6:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -7:
						break;
					case 7:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -8:
						break;
					case 8:
						{ return new Symbol(TokenConstants.MINUS); }
					case -9:
						break;
					case 9:
						{ return new Symbol(TokenConstants.LPAREN); }
					case -10:
						break;
					case 10:
						{ return new Symbol(TokenConstants.MULT); }
					case -11:
						break;
					case 11:
						{ return new Symbol(TokenConstants.RPAREN); }
					case -12:
						break;
					case 12:
						{ return new Symbol(TokenConstants.SEMI); }
					case -13:
						break;
					case 13:
						{ return new Symbol(TokenConstants.LT); }
					case -14:
						break;
					case 14:
						{ return new Symbol(TokenConstants.COMMA); }
					case -15:
						break;
					case 15:
						{ return new Symbol(TokenConstants.DIV); }
					case -16:
						break;
					case 16:
						{ return new Symbol(TokenConstants.PLUS); }
					case -17:
						break;
					case 17:
						{ return new Symbol(TokenConstants.DOT); }
					case -18:
						break;
					case 18:
						{ return new Symbol(TokenConstants.EQ); }
					case -19:
						break;
					case 19:
						{ return new Symbol(TokenConstants.COLON); }
					case -20:
						break;
					case 20:
						{ return new Symbol(TokenConstants.LBRACE); }
					case -21:
						break;
					case 21:
						{ return new Symbol(TokenConstants.RBRACE); }
					case -22:
						break;
					case 22:
						{ return new Symbol(TokenConstants.NEG); }
					case -23:
						break;
					case 23:
						{ return new Symbol(TokenConstants.AT); }
					case -24:
						break;
					case 24:
						{ return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext())); }
					case -25:
						break;
					case 25:
						{ return new Symbol(TokenConstants.FI); }
					case -26:
						break;
					case 26:
						{ return new Symbol(TokenConstants.IF); }
					case -27:
						break;
					case 27:
						{ return new Symbol(TokenConstants.IN); }
					case -28:
						break;
					case 28:
						{ return new Symbol(TokenConstants.OF); }
					case -29:
						break;
					case 29:
						{ yybegin(COMMENT); }
					case -30:
						break;
					case 30:
						{ yybegin(BLOCK_COMMENT); }
					case -31:
						break;
					case 31:
						{ return new Symbol(TokenConstants.ERROR, "Unmatched *)"); }
					case -32:
						break;
					case 32:
						{ return new Symbol(TokenConstants.ASSIGN); }
					case -33:
						break;
					case 33:
						{ return new Symbol(TokenConstants.LE); }
					case -34:
						break;
					case 34:
						{ return new Symbol(TokenConstants.DARROW); }
					case -35:
						break;
					case 35:
						{ return new Symbol(TokenConstants.LET); }
					case -36:
						break;
					case 36:
						{ return new Symbol(TokenConstants.NEW); }
					case -37:
						break;
					case 37:
						{ return new Symbol(TokenConstants.NOT); }
					case -38:
						break;
					case 38:
						{ return new Symbol(TokenConstants.BOOL_CONST, new Boolean(true)); }
					case -39:
						break;
					case 39:
						{ return new Symbol(TokenConstants.THEN); }
					case -40:
						break;
					case 40:
						{ return new Symbol(TokenConstants.ELSE); }
					case -41:
						break;
					case 41:
						{ return new Symbol(TokenConstants.ESAC); }
					case -42:
						break;
					case 42:
						{ return new Symbol(TokenConstants.LOOP); }
					case -43:
						break;
					case 43:
						{ return new Symbol(TokenConstants.CASE); }
					case -44:
						break;
					case 44:
						{ return new Symbol(TokenConstants.POOL); }
					case -45:
						break;
					case 45:
						{ return new Symbol(TokenConstants.BOOL_CONST, new Boolean(false)); }
					case -46:
						break;
					case 46:
						{ return new Symbol(TokenConstants.CLASS); }
					case -47:
						break;
					case 47:
						{ return new Symbol(TokenConstants.WHILE); }
					case -48:
						break;
					case 48:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -49:
						break;
					case 49:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -50:
						break;
					case 50:
						{
						if (!appendAndCheckLength(yytext())) {
							yybegin(STRING_ERROR);
							return new Symbol(TokenConstants.ERROR, "String constant too long");
						}
					}
					case -51:
						break;
					case 51:
						{ yybegin(YYINITIAL); return new Symbol(TokenConstants.STR_CONST, AbstractTable.stringtable.addString(string_buf.toString())); }
					case -52:
						break;
					case 52:
						{ /* If newline encountered inside string literal, assume forgotten ", continue at next line */ 
						curr_lineno++; 
						yybegin(YYINITIAL);
						return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
					}
					case -53:
						break;
					case 53:
						{ yybegin(STRING_ERROR); return new Symbol(TokenConstants.ERROR, "String contains null character"); }
					case -54:
						break;
					case 54:
						{ 
						if ((int)yytext().charAt(1) == 0) { /* Null char in string */
							yybegin(STRING_ERROR);
							return new Symbol(TokenConstants.ERROR, "String contains null character");
						}
						if (!appendAndCheckLength(getASCII(yytext().charAt(1)))) {
							yybegin(STRING_ERROR);
							return new Symbol(TokenConstants.ERROR, "String constant too long");
						}
					}
					case -55:
						break;
					case 55:
						{ 
						if (!appendAndCheckLength('\n')) {
							yybegin(STRING_ERROR);
							return new Symbol(TokenConstants.ERROR, "String constant too long");
						}
					}
					case -56:
						break;
					case 56:
						{ }
					case -57:
						break;
					case 57:
						{ curr_lineno++; yybegin(YYINITIAL); }
					case -58:
						break;
					case 58:
						{ }
					case -59:
						break;
					case 59:
						{ curr_lineno++; }
					case -60:
						break;
					case 60:
						{ }
					case -61:
						break;
					case 61:
						{ nestedCommentCount++; }
					case -62:
						break;
					case 62:
						{
						if (nestedCommentCount == 0) 	{ yybegin(YYINITIAL); }
						else 				{ nestedCommentCount--; }
					}
					case -63:
						break;
					case 63:
						{ curr_lineno++; yybegin(YYINITIAL); }
					case -64:
						break;
					case 64:
						{ }
					case -65:
						break;
					case 66:
						{ return new Symbol(TokenConstants.ERROR, yytext()); }
					case -66:
						break;
					case 67:
						{ }
					case -67:
						break;
					case 68:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -68:
						break;
					case 69:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -69:
						break;
					case 70:
						{ return new Symbol(TokenConstants.FI); }
					case -70:
						break;
					case 71:
						{ return new Symbol(TokenConstants.IF); }
					case -71:
						break;
					case 72:
						{ return new Symbol(TokenConstants.IN); }
					case -72:
						break;
					case 73:
						{ return new Symbol(TokenConstants.OF); }
					case -73:
						break;
					case 74:
						{ return new Symbol(TokenConstants.LET); }
					case -74:
						break;
					case 75:
						{ return new Symbol(TokenConstants.NEW); }
					case -75:
						break;
					case 76:
						{ return new Symbol(TokenConstants.NOT); }
					case -76:
						break;
					case 77:
						{ return new Symbol(TokenConstants.THEN); }
					case -77:
						break;
					case 78:
						{ return new Symbol(TokenConstants.ELSE); }
					case -78:
						break;
					case 79:
						{ return new Symbol(TokenConstants.ESAC); }
					case -79:
						break;
					case 80:
						{ return new Symbol(TokenConstants.LOOP); }
					case -80:
						break;
					case 81:
						{ return new Symbol(TokenConstants.CASE); }
					case -81:
						break;
					case 82:
						{ return new Symbol(TokenConstants.POOL); }
					case -82:
						break;
					case 83:
						{ return new Symbol(TokenConstants.CLASS); }
					case -83:
						break;
					case 84:
						{ return new Symbol(TokenConstants.WHILE); }
					case -84:
						break;
					case 85:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -85:
						break;
					case 86:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -86:
						break;
					case 87:
						{
						if (!appendAndCheckLength(yytext())) {
							yybegin(STRING_ERROR);
							return new Symbol(TokenConstants.ERROR, "String constant too long");
						}
					}
					case -87:
						break;
					case 88:
						{ }
					case -88:
						break;
					case 89:
						{ }
					case -89:
						break;
					case 91:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -90:
						break;
					case 92:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -91:
						break;
					case 93:
						{
						if (!appendAndCheckLength(yytext())) {
							yybegin(STRING_ERROR);
							return new Symbol(TokenConstants.ERROR, "String constant too long");
						}
					}
					case -92:
						break;
					case 94:
						{ }
					case -93:
						break;
					case 96:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -94:
						break;
					case 97:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -95:
						break;
					case 98:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -96:
						break;
					case 99:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -97:
						break;
					case 100:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -98:
						break;
					case 101:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -99:
						break;
					case 102:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -100:
						break;
					case 103:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -101:
						break;
					case 104:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -102:
						break;
					case 105:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -103:
						break;
					case 106:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -104:
						break;
					case 107:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -105:
						break;
					case 108:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -106:
						break;
					case 109:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -107:
						break;
					case 110:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -108:
						break;
					case 111:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -109:
						break;
					case 112:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -110:
						break;
					case 113:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -111:
						break;
					case 114:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -112:
						break;
					case 115:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -113:
						break;
					case 116:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -114:
						break;
					case 117:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -115:
						break;
					case 118:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -116:
						break;
					case 119:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -117:
						break;
					case 120:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -118:
						break;
					case 121:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -119:
						break;
					case 122:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -120:
						break;
					case 123:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -121:
						break;
					case 124:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -122:
						break;
					case 125:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -123:
						break;
					case 126:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -124:
						break;
					case 127:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -125:
						break;
					case 128:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -126:
						break;
					case 129:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -127:
						break;
					case 130:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -128:
						break;
					case 131:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -129:
						break;
					case 132:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -130:
						break;
					case 133:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -131:
						break;
					case 134:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -132:
						break;
					case 135:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -133:
						break;
					case 136:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -134:
						break;
					case 137:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -135:
						break;
					case 138:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -136:
						break;
					case 139:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -137:
						break;
					case 140:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -138:
						break;
					case 141:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -139:
						break;
					case 142:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -140:
						break;
					case 143:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -141:
						break;
					case 144:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -142:
						break;
					case 145:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -143:
						break;
					case 146:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -144:
						break;
					case 147:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -145:
						break;
					case 148:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -146:
						break;
					case 149:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -147:
						break;
					case 150:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -148:
						break;
					case 151:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -149:
						break;
					case 152:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -150:
						break;
					case 153:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -151:
						break;
					case 154:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -152:
						break;
					case 155:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -153:
						break;
					case 156:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -154:
						break;
					case 157:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -155:
						break;
					case 158:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -156:
						break;
					case 159:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -157:
						break;
					case 160:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -158:
						break;
					case 161:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -159:
						break;
					case 162:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -160:
						break;
					case 163:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -161:
						break;
					case 164:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -162:
						break;
					case 165:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -163:
						break;
					case 166:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -164:
						break;
					case 167:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -165:
						break;
					case 168:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -166:
						break;
					case 169:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -167:
						break;
					case 170:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -168:
						break;
					case 171:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -169:
						break;
					case 172:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -170:
						break;
					case 173:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -171:
						break;
					case 174:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -172:
						break;
					case 175:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -173:
						break;
					case 176:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -174:
						break;
					case 177:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -175:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
