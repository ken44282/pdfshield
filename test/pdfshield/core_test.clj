(ns pdfshield.core-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [pdfshield.core :refer :all])
  (:import [java.io FileInputStream FileOutputStream]))

(def fout (io/as-file "resources/result.pdf"))

(deftest test-set-text
  (testing "test set-text"
    (when (.exists fout) (.delete fout))
    (let [fis (FileInputStream. "resources/test.pdf")
          byte-arr (set-text fis 1 20 100 200 "テストテスト")]
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close)))
    (is (.exists fout))))
