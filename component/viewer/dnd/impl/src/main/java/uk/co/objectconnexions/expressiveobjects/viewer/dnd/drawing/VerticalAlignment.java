package uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing;

/**
 * Each element provides an align method that returns baseline position to use
 * when rendering text so that is positioned relative to the specified top and
 * bottom edges.
 * 
 */
public enum VerticalAlignment {
	TOP {
		@Override
		public int align(Text style, int topEdge, int height) {
			return topEdge + style.getAscent();
		}
	},
	BOTTOM {
		@Override
		public int align(Text style, int topEdge, int height) {
			return topEdge + height - 1 - style.getDescent();
		}
	},
	CENTER {
		@Override
		public int align(Text style, int topEdge, int height) {
		//	int height = height - topEdge;
			int center = height / 2;
			return topEdge + center + style.getTextHeight() / 2 - style.getDescent();
		}
	},
	BASELINE {
		@Override
		public int align(Text style, int topEdge, int height) {
			return topEdge + height - 1;
		}
	};

	public abstract int align(Text style, int topEdge, int height);

}
