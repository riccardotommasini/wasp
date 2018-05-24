package it.polimi.sr.wasp.rsp.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QueryBody {

    public String id;
    public String body;
    public String output_stream;
    public String[] input_streams;

}
