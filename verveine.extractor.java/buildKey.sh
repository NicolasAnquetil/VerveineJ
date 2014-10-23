#!/bin/bash

wc -c lib/famix.jar | awk '{print $1}' > lib/key
