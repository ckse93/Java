import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//  NOMENCLATOR. Read names from a Java source file.  It acts like an ITERATOR,
//  but it has two NEXT methods: one for names, and one for the line numbers of
//  those names.
class Nomenclator {
	private char ch; // Current CHAR from READER.
	private static final char eof = (char) 0x00; // End of file sentinel.
	private static final char eol = (char) 0x0A; // End of line sentinel.
	private int index; // Index into LINE.
	private String line; // Current LINE from READER.
	private boolean listing; // Are we listing the file?
	private String name; // Current name.
	private int number; // Current line number.
	private String path; // Pathname to READER's file.
	private BufferedReader reader; // Read CHARs from here.
	// Constructor. Initialize a new NOMENCLATOR that reads from a text file whose
	// pathname is PATH. If we can't open it then throw an exception. LISTING says
	// whether we should copy the file to standard output as we read it.
	public Nomenclator(String path, boolean listing) {
		try {
			index = 0;
			line = "";
			this.listing = listing;
			number = 0;
			this.path = path;
			reader = new BufferedReader(new FileReader(path));
			skipChar();
		} catch (IOException ignore) {
			throw new IllegalArgumentException("Can't open '" + path + "'.");
		}
	}
	// HAS NEXT. Test if there's another name waiting to be read. If so, then read
	// it, so NEXT NAME and NEXT NUMBER can return it and its line number later.
	public boolean hasNext() {
		while (true) {
			if (Character.isJavaIdentifierStart(ch)) {
				skipName();
				return true;
			} else if (Character.isDigit(ch)) {
				skipNumber();
			} else {
				switch (ch) {
				case eof: {
					return false;
				}
				case '"':
				case '\'': {
					skipDelimited();
					break;
				}
				case '/': {
					skipComment();
					break;
				}
				default: {
					skipChar();
					break;
				}
				}
			}
		}
	}
	// NEXT NAME. If HAS NEXT was true, then return the next name. If HAS NEXT was
	// false, then return an undefined string.
	public String nextName() {
		return name;
	}
	// NEXT NUMBER. If HAS NEXT was true, then return the line number on which the
	// next name appears. If HAS NEXT was false, then return an undefined INT.
	public int nextNumber() {
		return number;
	}
	// SKIP CHAR. If no more CHARs remain unread in LINE, then read the next line,
	// adding an EOL at the end. If no lines can be read, then read a line with an
	// EOF char in it. Otherwise just read the next char from LINE and return it.
	private void skipChar() {
		if (index >= line.length()) {
			index = 0;
			number += 1;
			try {
				line = reader.readLine();
				if (line == null) {
					line = "" + eof;
				} else {
					if (listing) {
						System.out.format("%05d ", number);
						System.out.println(line);
					}
					line += eol;
				}
			} catch (IOException ignore) {
				line = "" + eof;
			}
		}
		ch = line.charAt(index);
		index += 1;
	}
	// SKIP COMMENT. We end up here if we read a '/'. If it is followed by another
	// '/', or by a '*', then we skip a comment. We must skip comments so that any
	// names that appear in them will be ignored.
	private void skipComment() {
		skipChar();
		if (ch == '*') {
			skipChar();
			while (true) {
				if (ch == '*') {
					skipChar();
					if (ch == '/') {
						skipChar();
						return;
					}
				} else if (ch == eof) {
					return;
				} else {
					skipChar();
				}
			}
		} else if (ch == '/') {
			skipChar();
			while (true) {
				if (ch == eof) {
					return;
				} else if (ch == eol) {
					skipChar();
					return;
				} else {
					skipChar();
				}
			}
		}
	}
	// SKIP DELIMITED. Skip a string constant or a character constant, so that any
	// names that appear inside them will be ignored. Throw an exception if there
	// is a missing delimiter at the end.
	private void skipDelimited() {
		char delimiter = ch;
		skipChar();
		while (true) {
			if (ch == delimiter) {
				skipChar();
				return;
			} else {
				switch (ch) {
				case eof:
				case eol: {
					throw new IllegalStateException("Bad string in '" + path + "'.");
				}
				case '\\': {
					skipChar();
					if (ch == eol || ch == eof) {
						throw new IllegalStateException("Bad string in '" + path + "'.");
					} else {
						skipChar();
					}
					break;
				}
				default: {
					skipChar();
					break;
				}
				}
			}
		}
	}
	// SKIP NAME. Skip a name, but convert it to a STRING, stored in NAME.
	private void skipName() {
		StringBuilder builder = new StringBuilder();
		while (Character.isJavaIdentifierPart(ch)) {
			builder.append(ch);
			skipChar();
		}
		name = builder.toString();
	}
	// SKIP NUMBER. Skip something that might be a number. It starts with a digit,
	// followed by zero or more letters and digits. We must do this so the letters
	// aren't treated as names.
	private void skipNumber() {
		skipChar();
		while (Character.isJavaIdentifierPart(ch)) {
			skipChar();
		}
	}
	// MAIN. Get a file pathname from the command line. Read a series of names and
	// their line numbers from the file, and write them one per line. 
	// This method is only for debugging!
	
//======================================= MAIN ==================================================================
	
	public static void main(String[] args) {
		// debuging function.
//		Nomenclator reader = new Nomenclator("C:\\Users\\ckse9\\eclipse-workspace\\progect 3\\src\\Factorial.java",
//				false);
//		while (reader.hasNext()) {
//			System.out.println(reader.nextNumber() + " " + reader.nextName());
//		}
		BinarySearchTree tree = new BinarySearchTree();
		Nomenclator nomenclator = new Nomenclator(
				"C:\\\\Users\\\\ckse9\\\\eclipse-workspace\\\\progect 3\\\\src\\\\Factorial.java", false);
		while (nomenclator.hasNext()) {
			//System.out.println("next line exists");
			tree.add(nomenclator.nextNumber(), nomenclator.nextName());
		}
		tree.traverse();

	}
}

//======================================= MAIN ==================================================================


//BINARYSEATCHTREE establising BinarySearchTree with LinearQueue for line number value
class BinarySearchTree {
	class TreeNode {
		private String line; // each word 
		private TreeNode left; // left child, duh
		private TreeNode right; // right child 
		private Node last;//linear node attached to TreeNode, used for storing line numbers
		
		public TreeNode(String line, Node value) {
			this.line = line;
			this.right = null;
			this.left = null;
			this.last = value;
		}
	}
// NODE for LinearNode, no need for head node for now, add head node as you need
	private class Node { 
		private int num;
		private Node next;

		public Node(int num, Node next) {
			this.num = num;
			this.next = next;
		}
	}
	
	private TreeNode root;
// BST Constructor, initializing variable root, type TreeNode to null 
	public BinarySearchTree() {
		root = null;
	}
//ADD, storing String in TreeNode, attaching Node what has a line number value to that TreeNode
	public void add(int num, String line) {
		TreeNode temp = root;
		if (line == " " || line == "") { // Skip empty space 
			return;
		} else {
			if (root == null) { // Initializing
				root = new TreeNode(line, new Node(num, null));
				System.out.print("initial node added : ");
				System.out.println("<" + line + ", " + num + ">");
			} else {
				while (true) {
					System.out.println(line);
					int test = line.compareTo(temp.line);
					System.out.println(line.compareTo(temp.line));
					if (test < 0) { // input line is smaller than existing, goes to left
						if (temp.left == null) {
							temp.left = new TreeNode(line, new Node(num, null));
						System.out.print(line + " is smaller than " + temp.line + ", Node added : ");
							System.out.println("<" + line + ", " + num + ">");
							return;
						} 
						else {
							temp = temp.left;
						}
					} 
					else if (test > 0) {
						if (temp.right == null) {
							temp.right = new TreeNode(line, new Node(num, null));
							System.out.print(line + " is bigger than " + temp.line + ", Node added : ");
							System.out.println("<" + line + ", " + num);
							return;
						} 
						else {
							temp = temp.right;
						}
					} 
					else { // when line is same
						if (num == temp.last.num) {
							return;
						}
							Node spare = temp.last;
							temp.last.next = new Node(num, spare);
						
						System.out.println(temp.last.next.num);
						System.out.print(line + " is same as " + temp.line + ", Node added : ");
						System.out.println("<" + line + ", " + num + ">");
						return;
					}
				}
			}
		}
	}

	public void traverse() {
		traverse(root);
	}
	
//DEBUGGING METHOD, MAY GET NullPointerException IF NOT USED WITH Factorial.java
	public void showRoot() { 
		System.out.println(root.line);
		System.out.println(root.left.line + " is left");
		System.out.println(root.left.right.line + " is left.right");
		System.out.println(root.left.right.right.line + " is left.right.right");
		System.out.println(root.left.right.right.left.line + " is left.right.right.left");
		System.out.println(root.right.line + " is right");
		System.out.println(root.right.left.line + " is right.left");
	}
//TRAVERSE, using Inorder traversal.
	private void traverse(TreeNode root) {

		TreeNode temp = root;
		if (temp == null) {
			return;
		} 
		else {
			traverse (temp.left);
			System.out.print(temp.line);
			for (int i = 0; i < 20 - temp.line.length(); i++) {
				System.out.print(" ");
			}
			while (temp.last != null) {
				System.out.format("%05d", temp.last.num); // looking good
				System.out.print(" ");
				temp.last = temp.last.next;
			}
			System.out.println("");// for line separation
			//traverse (temp.right);
		}
	}
}