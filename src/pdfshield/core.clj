(ns pdfshield.core
  (:import [java.io ByteArrayOutputStream File FileInputStream FileOutputStream]
           [org.apache.fontbox.ttf TrueTypeCollection]
           [org.apache.pdfbox.pdmodel.graphics.image PDImageXObject]
           [org.apache.pdfbox.pdmodel PDDocument PDPageContentStream PDPageContentStream$AppendMode]
           [org.apache.pdfbox.pdmodel.font PDType0Font])
  (:gen-class))

(defn set-text [contents page-num size x y text]
  (let [document (PDDocument/load contents)
        page (.getPage document page-num)
        ttc (TrueTypeCollection. (File. "resources/msgothic.ttc"))
        ttf (.getFontByName ttc "MS-Gothic")
        font (PDType0Font/load document ttf true)
        bos (ByteArrayOutputStream.)]
    (doto (PDPageContentStream. document page PDPageContentStream$AppendMode/APPEND true true)
      (.beginText)
      (.setFont font size)
      (.newLineAtOffset x y)
      (.showText text)
      (.endText)
      (.close))
    (.save document bos)
    (.close document)
    (.toByteArray bos)))

(defn test-set-text []
  (let [fis (FileInputStream. "resources/test.pdf")
        byte-arr (set-text fis 1 20 100 200 "テストテスト")]
    (doto (FileOutputStream. "resources/test2.pdf")
      (.write byte-arr)
      (.close))))

(defn set-image [contents img-byte page-num x y, width, height]
  (let [document (PDDocument/load contents)
        page (.getPage document page-num)
        img (PDImageXObject/createFromByteArray document img-byte "img")
        bos (ByteArrayOutputStream.)]
    (doto (PDPageContentStream. document page PDPageContentStream$AppendMode/APPEND true true)
      (.drawImage img x y width height)
      (.close))
    (.save document bos)
    (.close document)
    (.toByteArray bos)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
