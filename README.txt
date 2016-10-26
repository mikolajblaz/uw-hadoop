Autor: Mikołaj Błaż
27.10.2016

W katalogu 'scripts' znajdują się skrypty, w celu pełnego wystartowania hadoopa
(łącznie z pobraniem) należy uruchomić skrypt 'run_all.sh' z argumentami 'master slaves'.

Policzenie shingli w różnych plikach - klasa 'Summary' z folderem z plikami jako jednym argumentem, np.
yarn jar Summary.jar Summary /input/books

LSH - klasa 'Similarity' z folderem z plikami jako jednym argumentem i folderem na wyjście jako drugim, np.
yarn jar Similarity.jar Similarity /input/books /output

UWAGA: żeby policzyć powyższe rzeczy na pliku z tweetami (czyli jednym pliku CSV z wieloma dokumentami wewnątrz)
należy uruchomić odpowiadające powyższym klasom klasy 'SummaryCSV' i 'SimilarityCSV' z takimi samymi argumentami.