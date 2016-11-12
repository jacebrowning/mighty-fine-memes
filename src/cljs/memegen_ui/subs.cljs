(ns memegen-ui.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :initialized?
 (fn [db]
   (:initialized? db)))

(re-frame/reg-sub
 :available-meme-templates
 (fn [db]
   (:available-meme-templates db)))