package com.example.javafx_reactivo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.example.javafx_reactivo.modelos.Usuario;
import com.example.javafx_reactivo.modelos.ApiService;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

import java.io.IOException;

public class HelloApplication extends Application {

    private final ApiService apiService = new ApiService();

    // 1. Colección observable con EXTRACTOR: la tabla se repinta si muta cualquier propiedad del objeto
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList(
            usuario -> new Observable[]{
                    usuario.nameProperty(),
                    usuario.usernameProperty(),
                    usuario.emailProperty(),
                    usuario.phoneProperty()
            }
    );

    // Componentes visuales
    private TableView<Usuario> tabla;
    private Label lblEstado;
    private ProgressIndicator indicadorProgreso;

    // Formulario
    private TextField txtId;
    private TextField txtNombre;
    private TextField txtUsername;
    private TextField txtEmail;
    private TextField txtTelefono;

    private Button btnGuardar;
    private Button btnEliminar;
    private Button btnLimpiar;
    private Button btnCargarApi;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(14));

        // Configurar TableView y vincularlo a la lista observable
        tabla = new TableView<>();
        configurarColumnas();
        tabla.setItems(listaUsuarios); // Enlace reactivo del modelo a la vista

        // Formulario y enlaces reactivos
        VBox panelFormulario = crearPanelFormulario();
        HBox barraSuperior = crearBarraSuperior();

        root.setTop(barraSuperior);
        root.setCenter(tabla);
        root.setRight(panelFormulario);
        BorderPane.setMargin(panelFormulario, new Insets(0, 0, 0, 14));

        configurarReactividad();

        Scene scene = new Scene(root, 960, 530);
        primaryStage.setTitle("CRUD Reactivo JavaFX con REST API (ObservableList + Properties)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void configurarColumnas() {
        TableColumn<Usuario, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(55);

        TableColumn<Usuario, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colNombre.setPrefWidth(170);

        TableColumn<Usuario, String> colUsuario = new TableColumn<>("Usuario");
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUsuario.setPrefWidth(110);

        TableColumn<Usuario, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(180);

        TableColumn<Usuario, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colTelefono.setPrefWidth(140);

        tabla.getColumns().addAll(colId, colNombre, colUsuario, colEmail, colTelefono);
    }

    private HBox crearBarraSuperior() {
        btnCargarApi = new Button("Sincronizar con REST API");
        btnCargarApi.setOnAction(e -> descargarDesdeApiReactiva());

        indicadorProgreso = new ProgressIndicator();
        indicadorProgreso.setPrefSize(22, 22);
        indicadorProgreso.setVisible(false);

        lblEstado = new Label("Listo para operar.");

        HBox barra = new HBox(12, btnCargarApi, indicadorProgreso, lblEstado);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(0, 0, 10, 0));
        return barra;
    }

    private VBox crearPanelFormulario() {
        VBox panel = new VBox(8);
        panel.setPrefWidth(270);

        Label lblTitulo = new Label("Ficha de Usuario");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        txtId = new TextField();
        txtId.setPromptText("ID (Automático)");
        txtId.setDisable(true);

        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre completo");

        txtUsername = new TextField();
        txtUsername.setPromptText("Nombre de usuario");

        txtEmail = new TextField();
        txtEmail.setPromptText("correo@empresa.com");

        txtTelefono = new TextField();
        txtTelefono.setPromptText("Teléfono");

        btnGuardar = new Button("Guardar");
        btnGuardar.setDefaultButton(true);
        btnGuardar.setOnAction(e -> handleGuardar());

        btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold;");
        btnEliminar.setOnAction(e -> handleEliminar());

        btnLimpiar = new Button("Limpiar");
        btnLimpiar.setOnAction(e -> handleLimpiar());

        HBox acciones = new HBox(6, btnGuardar, btnEliminar, btnLimpiar);

        panel.getChildren().addAll(
                lblTitulo,
                new Label("ID:"), txtId,
                new Label("Nombre:"), txtNombre,
                new Label("Usuario:"), txtUsername,
                new Label("Email:"), txtEmail,
                new Label("Teléfono:"), txtTelefono,
                new Separator(),
                acciones
        );

        return panel;
    }

    /**
     * Enlaces y bindings reactivos entre controles de la interfaz.
     */
    private void configurarReactividad() {
        // 1. Reacción al cambio de selección en la tabla
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtId.setText(String.valueOf(seleccionado.getId()));
                txtNombre.setText(seleccionado.getName());
                txtUsername.setText(seleccionado.getUsername());
                txtEmail.setText(seleccionado.getEmail());
                txtTelefono.setText(seleccionado.getPhone());
            }
        });

        // 2. Reactividad en botones mediante Bindings lógicos
        // El botón eliminar solo se habilita si hay una fila seleccionada
        btnEliminar.disableProperty().bind(tabla.getSelectionModel().selectedItemProperty().isNull());

        // El botón guardar se deshabilita reactivamente si Nombre o Email están vacíos
        BooleanBinding camposInvalidos = Bindings.createBooleanBinding(
                () -> txtNombre.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty(),
                txtNombre.textProperty(),
                txtEmail.textProperty()
        );
        btnGuardar.disableProperty().bind(camposInvalidos);
    }

    /**
     * Operación CREATE / UPDATE reactiva.
     */
    private void handleGuardar() {
        if (txtId.getText().isEmpty()) {
            // CREATE: Al añadir a la ObservableList, la tabla añade la fila instantáneamente
            int nuevoId = listaUsuarios.stream().mapToInt(Usuario::getId).max().orElse(0) + 1;
            Usuario nuevo = new Usuario(
                    nuevoId,
                    txtNombre.getText().trim(),
                    txtUsername.getText().trim(),
                    txtEmail.getText().trim(),
                    txtTelefono.getText().trim()
            );
            listaUsuarios.add(nuevo);
            lblEstado.setText("Usuario creado: ID " + nuevoId);
        } else {
            // UPDATE: Modificamos las propiedades del objeto directamente en memoria.
            // Gracias al extractor en ObservableList, la tabla actualiza su fila automáticamente.
            Usuario seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                seleccionado.setName(txtNombre.getText().trim());
                seleccionado.setUsername(txtUsername.getText().trim());
                seleccionado.setEmail(txtEmail.getText().trim());
                seleccionado.setPhone(txtTelefono.getText().trim());
                lblEstado.setText("Usuario ID " + seleccionado.getId() + " actualizado reactivamente.");
            }
        }
        handleLimpiar();
    }

    /**
     * Operación DELETE reactiva.
     */
    private void handleEliminar() {
        Usuario seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            // Al remover de la ObservableList, la fila desaparece de la vista automáticamente
            listaUsuarios.remove(seleccionado);
            lblEstado.setText("Usuario ID " + seleccionado.getId() + " eliminado.");
            handleLimpiar();
        }
    }

    private void handleLimpiar() {
        txtId.clear();
        txtNombre.clear();
        txtUsername.clear();
        txtEmail.clear();
        txtTelefono.clear();
        tabla.getSelectionModel().clearSelection();
    }

    /**
     * Consumo asíncrono y reactivo de la REST API mediante JavaFX Task.
     */
    private void descargarDesdeApiReactiva() {
        Task<List<Usuario>> tareaDescarga = apiService.crearTareaDescarga();

        // Enlace reactivo del estado de la tarea con la interfaz visual
        lblEstado.textProperty().bind(tareaDescarga.messageProperty());
        indicadorProgreso.visibleProperty().bind(tareaDescarga.runningProperty());
        btnCargarApi.disableProperty().bind(tareaDescarga.runningProperty());

        // Callbacks de ciclo de vida en el JavaFX Application Thread
        tareaDescarga.setOnSucceeded(event -> {
            lblEstado.textProperty().unbind(); // Liberamos el binding para mensajes locales
            List<Usuario> resultados = tareaDescarga.getValue();
            listaUsuarios.setAll(resultados); // La vista se puebla reactivamente
            lblEstado.setText("Sincronizados " + resultados.size() + " registros desde la API.");
        });

        tareaDescarga.setOnFailed(event -> {
            lblEstado.textProperty().unbind();
            Throwable error = tareaDescarga.getException();
            lblEstado.setText("Fallo en sincronización: " + error.getMessage());

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error REST");
            alerta.setHeaderText("No fue posible consultar el servicio web");
            alerta.setContentText(error.getMessage());
            alerta.showAndWait();
        });

        // Lanzamiento en hilo secundario (worker thread)
        Thread hiloWorker = new Thread(tareaDescarga);
        hiloWorker.setDaemon(true);
        hiloWorker.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
