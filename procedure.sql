-- Procedure para desativar conta
DELIMITER $$
CREATE PROCEDURE desativar_conta (
    IN p_numero INT,
    OUT p_status BOOLEAN
)
BEGIN
    UPDATE conta SET esta_ativa = FALSE WHERE numero = p_numero;
    SELECT esta_ativa INTO p_status FROM conta WHERE numero = p_numero;
END $$
DELIMITER ;

