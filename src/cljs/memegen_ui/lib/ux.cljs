;; scroll-to-id is based on https://github.com/GabrielDelepine/smooth-scroll/blob/main/smooth-scroll.js
(ns memegen-ui.lib.ux)

(def speed 500)
(def moving-frequency 15)

(defn cur-doc-top []
  (+ (.. js/document -body -scrollTop) (.. js/document -documentElement -scrollTop)))

(defn element-top [elem top]
  (if (and elem (.-offsetParent elem))
    (let [client-top (or (.-clientTop elem) 0)
          offset-top (.-offsetTop elem)]
      (+ top client-top offset-top (element-top (.-offsetParent elem) top)))
    top))

(defn scroll-to-id
  [elem-id]
  (let [elem (.getElementById js/document elem-id)
        hop-count (/ speed moving-frequency)
        doc-top (cur-doc-top)
        gap (/ (- (element-top elem 0) doc-top) hop-count)]
    (doseq [i (range 1 (inc hop-count))]
      (let [hop-top-pos (* gap i)
            move-to (+ hop-top-pos doc-top)
            timeout (* moving-frequency i)]
        (.setTimeout js/window (fn []
                                 (.scrollTo js/window 0 move-to))
                     timeout)))))

(defn copy-text-from-elem
  [elem-id]
  (let [elem (.getElementById js/document elem-id)]
    (.select elem)
    (try
      (if (.execCommand js/document "copy")
        "Copied to Clipboard!!"
        "Sorry Couldn't Copy")
      (catch :default e
        "Sorry Couldn't Copy"))))
