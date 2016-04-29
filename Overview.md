# Overview Dolphins Integration Platform #

http://dolphinssc1.appspot.com/image/wiki/base.PNG

**Dolphins Integration Platform** - component orientied appliccation to write ETL data integration tools

**Platform** has 4 components:

  1. **Metadata** is containing many jobs, which includes:
    * **Rule** - file, write on my language components config to dataflow process
    * **Check** - Rule, which will finish by check component to validate condition
  1. **Engine** - component, which compile Rule to java byte-code and run dataflow or validation process
  1. **Scheduler**:
    * Start all Check rules -> validate success add corresponding rules to Queue
    * Start all rules from Queue
    * Log work process to database
  1. **Server** - web-base application to Scheduler database