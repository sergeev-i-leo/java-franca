package franca.java.ui.animations;

import franca.java.TranspilableClass;
import franca.java.ui.Router;
import franca.java.ui.Page;
import franca.java.ui.View;

public class Tween extends TranspilableClass {

  public Router router = null;
  public Page page = null;
  public View view = null;
  public Ease ease = null;

  public long registeredTime = 0L;
  public Tween previousTween = null;

  public Tween(Router router, Page page, View view, Ease ease) {
    super();

    this.router = router;
    this.page = page;
    this.view = view;
    this.ease = ease;
  }

  public void tick() {
    if (ease != null) {
      ease.tick(router, getElapsedTime());
    }
  }

  public long getElapsedTime() {
    return router.getTime() - registeredTime;
  }

  public boolean needsNextPainting() {
    if (ease == null) {
      // one shot animation
      return false;
    }
    return !ease.isOver();
  }

  public void onDone() {
  }
}
