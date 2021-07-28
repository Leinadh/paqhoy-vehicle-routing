import os


def formatear_arch_pedido(input_file, input_file_name, output_path):
    registros_formateados = []
    with open(input_file, 'r') as f:
        for line in f:
            split_line = line.split(',')
            new_line = ','.join(split_line[:3] + split_line[4:])
            for _ in range(int(split_line[3])):
                # print(new_line)
                registros_formateados.append(new_line)

    with open(os.path.join(output_path, input_file_name), 'w') as f:
        for registro in registros_formateados:
            f.write(registro)


if __name__ == '__main__':
    # input_path = '../Test Data/Test Data Sin Formato/data.historica.20210511/'
    # output_path = '../Test Data/pred.2021.pedidos/'
    #
    # pred_input_path = '../Test Data/Test Data Sin Formato/pred.2021.pedidos/'
    #
    # for file_name in os.listdir(input_path):
    #     print(file_name)
    #     formatear_arch_pedido(os.path.join(input_path, file_name), file_name, output_path)
    #
    # for file_name in os.listdir(pred_input_path):
    #     print(file_name)
    #     formatear_arch_pedido(os.path.join(pred_input_path, file_name), file_name, output_path)

    input_path = '../Test Data/Test Data Sin Formato/data.historica.20210710/'
    output_path = '../Test Data/formateado.data.20210710/formateado.data.historica.20210710'

    for file_name in os.listdir(input_path):
        print(file_name)
        formatear_arch_pedido(os.path.join(input_path, file_name), file_name, output_path)
