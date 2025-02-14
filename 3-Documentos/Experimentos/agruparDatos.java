{
        List<List<Object>> triosTFS = obtenerTrios();
        int[] iteraciones = {0,0,0,0,0,0};
        Map<Integer, Map<String, Map<Integer, Map<Integer, Map<Integer, String>>>>> agregados = new HashMap<>();
        int columnaInicial = 2;
        for(int i = 0 ; i < triosTFS.size() ; i++) {
            List<Object> trio = triosTFS.get(i);
            Integer size = Integer.parseInt(String.valueOf(trio.get(1)));
            String format = String.valueOf(trio.get(2));
            if(!agregados.containsKey(size)){
                Map<String, Map<Integer, Map<Integer, Map<Integer, String>>>> formatMap = new HashMap<>();
                agregados.put(size, formatMap);
            }else if(format.equals(".jpg")){
                iteraciones[size]++;
            }
            if(!agregados.get(size).containsKey(format)){
                Map<Integer, Map<Integer, Map<Integer, String>>> timeMap = new HashMap<>();
                agregados.get(size).put(format, timeMap);
            }
            int columnaMaximaConValor=0;
            for(int j = 0 ; j < 8 ; j++){
                if(i*8+j+1<ordenados.size()){
                    if(!agregados.get(size).get(format).containsKey(j)){
                        Map<Integer, Map<Integer, String>> iterationMap = new HashMap<>();
                        agregados.get(size).get(format).put(j, iterationMap);
                    }
                    if(!agregados.get(size).get(format).get(j).containsKey(iteraciones[size])){
                        Map<Integer, String> replicaMap = new HashMap<>();
                        agregados.get(size).get(format).get(j).put(iteraciones[size], replicaMap);
                    }
                    String valorString = ordenados.get(i*8+j+1).get(1);
                    if(!valorString.isBlank()){
                        if(!agregados.get(size).get(format).get(j).containsKey(iteraciones[0])){
                            Map<Integer, String> timeMap = new HashMap<>();
                            agregados.get(size).get(format).get(j).put(iteraciones[0], timeMap);
                        }
                        agregados.get(size).get(format).get(j).get(iteraciones[0]).put(1, valorString);
                    }
                    for(int k = 1 ; k < 4 ;k++){
                        valorString = ordenados.get(i*8+j+1).get(k+columnaInicial);
                        if(!valorString.isBlank()){
                            columnaMaximaConValor = Math.max(columnaMaximaConValor, k+columnaInicial);
                            agregados.get(size).get(format).get(j).get(iteraciones[0]).put(k, valorString);
                        }
                    }
                }
            }
            columnaInicial = Math.max(columnaInicial,columnaMaximaConValor);
        }

        return agregados;
    }