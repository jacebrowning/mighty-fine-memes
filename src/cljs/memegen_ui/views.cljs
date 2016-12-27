(ns memegen-ui.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [memegen-ui.ux :as ux]
            [reagent.core :as reagent]))


;;
;; Components
(defn meme-view [meme]
  [:div.col-md-3.col-xs-12.col-sm-6.meme-listing-view {:id (:name meme)
                                    :on-click #(dispatch [:meme-selected meme]) }
   [:div.meme-listing-view__meme
    [:span.meme-listing-view__meme__helper]
    [:img.meme-listing-view__meme__img {:src (:blank meme)
                                        :alt (:name meme)}]]
   [:div.meme-listing-view__name (:name meme)]])

(defn meme-editor
  [meme]
  (reagent/create-class
   {:component-did-mount
    #(ux/scroll-to-id "meme-editor")

    :display-name "meme-editor"

    :reagent-render
    (fn [meme]
      (let [top-text (subscribe [:top-text])
            bottom-text (subscribe [:bottom-text])
            meme-url (subscribe [:meme-url])]
        (fn []
          [:div.meme-editor {:id "meme-editor"}
           [:h3 "Generate Meme"]
           [:div.row
            [:div.col-sm-6
             [:div.meme-editor__view
              [:span.meme-editor__view__helper]
              [:a {:href @meme-url
                   :target "_blank"}
               [:img.meme-editor__view__image {:src @meme-url
                                               :alt (:name meme)}]]]]
            [:div.col-sm-6
             [:div.form-group
              [:label "Top Text"]
              [:input.form-control {:id "top-text"
                                    :value @top-text
                                    :autoFocus true
                                    :on-change #(dispatch [:top-text-updated (-> % .-target .-value)])}]]
             [:div.form-group
              [:label "Bottom Text"]
              [:input.form-control {:id "bottom-text"
                                    :value @bottom-text
                                    :on-change #(dispatch [:bottom-text-updated (-> % .-target .-value)])}]]
             [:div.meme-editor__actions
              [:button.btn.btn-default.pull-left {:type "button"
                                                  :on-click #(dispatch [:selection-closed])} "Done"]
              [:a.btn.btn-primary.pull-right {:href @meme-url
                                              :role "button"
                                              :target "_blank"} "Preview"]]]]])))}))

(defn meme-row-view [meme-row]
  (if (:selected meme-row)
    ^{:key (str "row-selected")}
    [:div.row.meme-listing__selected-row {:id "selected-row"}
     [meme-editor (:meme meme-row)]]
    ^{:key (str "row-" (:row-index meme-row))}
    [:div.row.meme-listing__meme-tuple
     (for [meme (:memes meme-row)]
       ^{:key (:name meme)} [meme-view meme])]))

(defn meme-listing []
  (let [meme-rows (subscribe [:filtered-meme-templates])]
    (fn []
      [:div.meme-listing.row
       [:div.col-xs-12
        (for [meme-row @meme-rows]
          (meme-row-view meme-row))]])))

(defn loading-view []
  [:div.loading-view
   [:span.loading-view__message "loading"]
   [:img.loading-view__spinner {:src "images/ellipsis.gif"}]])

(defn error-view []
  [:div.error-view
   [:span.error-view__message "An error has occurred :("]])

(defn top-panel []
  (let [ready? (subscribe [:initialized?])
        had-error? (subscribe [:had-error?])]
    (fn []
      (if @had-error?
        [error-view]
        (if-not @ready?
          [loading-view]
          [meme-listing])))))

(defn filter-bar []
  (let [filter-text (subscribe [:filter-text])]
    (fn []
      [:input.form-control {:type "text"
                            :placeholder "Filter memes by name"
                            :value @filter-text
                            :on-change #(dispatch [:filter-text-updated (-> % .-target .-value)])}])))

(defn main-panel []
  [:div
   [:div.row
    [:div.col-sm-4
     [:span.brand "Mighty Fine Memes"]]
    [:div.col-sm-8
     [filter-bar]]]
   [top-panel]])
