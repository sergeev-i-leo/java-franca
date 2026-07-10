import {BrowserRouter} from "./browser-router";

class Graphics2dRouter extends BrowserRouter {

  static fonts: Map<string, FontFace> = new Map();

  getTime(): number {
    return performance.now();
  }

  static async loadResources(): Promise<void> {

    try {
      const font = new FontFace("primary", require("~assets/fonts/andika_regular.ttf"));
      await font.load();
      Graphics2dRouter.fonts.set("primary", font);
    } catch (e) {
      console.warn(`Font inter_regular.ttf not loaded`, e);
    }
    try {
      const font = new FontFace("secondary", require("~assets/fonts/fira_sans_extra_condensed_bold.ttf"));
      await font.load();
      Graphics2dRouter.fonts.set("secondary", font);
    } catch (e) {
      console.warn(`Font fira_sans_extra_condensed_bold.ttf not loaded`, e);
    }
    try {
      const font = new FontFace("icons", require("~assets/fonts/remixicon.ttf"));
      await font.load();
      Graphics2dRouter.fonts.set("icons", font);
    } catch (e) {
      console.warn(`Font remixicon.ttf not loaded`, e);
    }
  }

}

export default Graphics2dRouter;
