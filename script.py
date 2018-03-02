# Extract per-query map and P_5 values into separate files.
files = ['PL2c1.0_0', 'te']
for file in files:
    with open('%s.eval' % (file), 'r') as src, open('%s-map.txt' % (file), 'w') as dst_map, open('%s-P_5.txt' % (file), 'w') as dst_P_5:
        for line in src.readlines():
            split_line = line.split('\t')
            measure_name = split_line[0]
            query = split_line[1]
            measure_value = split_line[2]
            if query != 'all':
                if measure_name == 'map':
                    dst_map.write(measure_value)
                elif measure_name == 'P_5':
                    dst_P_5.write(measure_value)
