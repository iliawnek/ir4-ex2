# Extract per-query map and P_5 values into separate files.
files = ['te', 'te+amd', 'te+dap', 'te+dap+amd']
for file in files:
    with open('%s.eval' % (file), 'r') as src, open('map_%s.txt' % (file), 'w') as dst:
        for line in src.readlines():
            split_line = line.split('\t')
            measure_name = split_line[0]
            query = split_line[1]
            measure_value = split_line[2]
            if query != 'all':
                if measure_name == 'map':
                    dst.write(measure_value)