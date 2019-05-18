package uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing;

public enum Alignment {
	LEFT {
		@Override
		public int align(String text, Text style, int leftEdge, int rightEdge) {
			return leftEdge;
		}
	},
	RIGHT {
		@Override
		public int align(String text, Text style, int leftEdge, int rightEdge) {
			final int pos = rightEdge - style.stringWidth(text);
			return pos > leftEdge ? pos : leftEdge;
		}
	},
	CENTER {
		@Override
		public int align(String text, Text style, int leftEdge, int rightEdge) {
			int width = rightEdge - leftEdge;
			final int pos = width / 2 - style.stringWidth(text) / 2;			
			return pos > leftEdge ? leftEdge + pos : leftEdge;
		}
	};
	
	public abstract int align(String text, Text style, int leftEdge, int rightEdge);

}
