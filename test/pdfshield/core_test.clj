(ns pdfshield.core-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [pdfshield.core :refer :all])
  (:import [java.io FileInputStream FileOutputStream]))

(deftest test-set-text
  (testing "test set-text"
    (let [fis (FileInputStream. "resources/test.pdf")
          byte-arr (set-text fis 1 20 100 200 "テストテスト")
          fout (io/as-file "resources/result-set-text.pdf")]
      (when (.exists fout) (.delete fout))
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close))
      (is (.exists fout)))))

(deftest test-set-image
  (testing "test set-image"
    (let [fis (FileInputStream. "resources/test.pdf")
          imgfis (FileInputStream. "resources/test.png")
          byte-arr (set-image fis imgfis 1 (float 20) (float 30) (float 100) (float 200))
          fout (io/as-file "resources/result-set-image.pdf")]
      (when (.exists fout) (.delete fout))
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close))
      (is (.exists fout)))))

(deftest test-extract-pages
  (testing "test extract-page"
    (let [fis (FileInputStream. "resources/test.pdf")
          byte-arr (extract-pages fis 2 4)
          fout (io/as-file "resources/result-extract-page.pdf")]
      (when (.exists fout) (.delete fout))
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close))
      (is (.exists fout)))))

(deftest test-merge-documents
  (testing "test merge-documents"
    (let [byte-arr (merge-documents (FileInputStream. "resources/aaa.pdf")
                                    (FileInputStream. "resources/bbb.pdf"))
          fout (io/as-file "resources/result-merge-documents.pdf")]
      (when (.exists fout) (.delete fout))
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close))
      (is (.exists fout)))))

(deftest test-remove-pages
  (testing "test remove-pages"
    (let [fis (FileInputStream. "resources/test.pdf")
          byte-arr (remove-pages fis 2 4)
          fout (io/as-file "resources/result-remove-page.pdf")]
      (when (.exists fout) (.delete fout))
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close))
      (is (.exists fout)))))

(deftest test-encrypt-document
  (testing "test encrypt-document"
    (let [fis (FileInputStream. "resources/test.pdf")
          byte-arr (encrypt-document fis "pass1" "pass2")
          fout (io/as-file "resources/result-encrypt-document.pdf")]
      (when (.exists fout) (.delete fout))
      (doto (FileOutputStream. fout)
        (.write byte-arr)
        (.close))
      (is (.exists fout)))))
