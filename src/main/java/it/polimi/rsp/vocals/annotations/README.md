A Feature interface must:

- be annotated with a @Feature annotation
  -  optionally can specify the namespace
- have only one method
  -  each parameter must be annotated with @Param annotation
  -  parameters are default put in the body
  -  complex classes are flattened in the body, 
  -  colliding fields/setter between classes may cause erroneous deserializations
- annotate that method with a @RSPService annotation
