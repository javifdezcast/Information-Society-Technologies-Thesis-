% Define the number of image sizes
num_sizes = 6; % Since we have ConsumoMemoriaJPG0 to ConsumoMemoriaJPG5

% Hold figure for multiple plots
figure; hold on;

varName = 'ReplicasPNG';
    
% Get the memory data
data = eval(varName); 

% Loop through each dataset
for i = 0:num_sizes-1
    
    % Extract memory consumption values
    memory_values = data; 
    num_replicas = size(memory_values, 2); % Number of replicas

    h = plot(TiempoReplicas, memory_values(:,i+1), 'Color', custom_colors(i+1,:), ...
        'LineWidth', 1, 'Marker', 'x', 'MarkerSize', 6);
    legend_handles(i+1) = h;
end

% Customize the plot
xlabel('Tiempo (s)');
ylabel('Tiempo de Respuesta (s)');
title('Replicas de la Funcion para Distintos Tamaños de Imagen');
legend(legend_handles, legend_labels, 'Location', 'best'); % Assign correct colors to legend
grid on;
hold off;
