#!/bin/bash

# Load data to MongoDB, both test data and an initial admin user.
mongo lookingfor mongo_local_lookingfor_script.js
mongo test mongo_local_test_script.js
