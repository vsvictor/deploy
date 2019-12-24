package ic.chatbot;


import org.jetbrains.annotations.Nullable;


public abstract class CarouselItem {

	public final @Nullable Button button = initButton();

	protected abstract @Nullable Button initButton();

	public abstract String getTitle();

	public abstract String getImageUrl();

}
