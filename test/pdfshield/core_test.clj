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

(deftest test-set-image
  (testing "test set-image"
    (when (.exists fout) (.delete fout))
    (let [fis (FileInputStream. "resources/test.pdf")
          img-ins (FileInputStream. "resources/test.png")
          byte-arr (set-image fis img-ins 1 (float 20) (float 30) (float 100) (float 200))]
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close)))
    (is (.exists fout))))

(deftest test-split-page
  (testing "test split-page"
    (when (.exists fout) (.delete fout))
    (let [fis (FileInputStream. "resources/test.pdf")
          byte-arr (split-page fis 2 4)]
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close)))
    (is (.exists fout))))

(deftest test-merge-documents
  (testing "test merge-documents"
    (when (.exists fout) (.delete fout))
    (let [byte-arr (merge-documents (FileInputStream. "resources/aaa.pdf")
                                    (FileInputStream. "resources/bbb.pdf"))]
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close)))
    (is (.exists fout))))

(deftest test-remove-page
  (testing "test remove-page"
    (when (.exists fout) (.delete fout))
    (let [fis (FileInputStream. "resources/test.pdf")
          byte-arr (remove-page fis 2 4)]
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close)))
    (is (.exists fout))))

(deftest test-encrypt-page
  (testing "test encrypt-page"
    (when (.exists fout) (.delete fout))
    (let [fis (FileInputStream. "resources/test.pdf")
          byte-arr (encrypt-page fis "pass1" "pass2")]
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close)))
    (is (.exists fout))))
