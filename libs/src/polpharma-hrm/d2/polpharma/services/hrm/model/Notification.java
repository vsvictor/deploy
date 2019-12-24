package d2.polpharma.services.hrm.model;


import org.jetbrains.annotations.NotNull;



public class Notification {


	public static final String TYPE_ACHIEVEMENT = "ACHIEVEMENT";

	public final String type;


	public static final String CATEGORY_COMMON 		= "COMMON";
	public static final String CATEGORY_EDUCATION 	= "EDUCATION";
	public static final String CATEGORY_WIKI 		= "WIKI";

	public final String category;

	public final boolean important;

	public final String content;

	public Notification(@NotNull String type, @NotNull String category, boolean important, @NotNull String content) {

		this.type 		= type;
		this.category 	= category;
		this.important 	= important;
		this.content 	= content;

	}


}
