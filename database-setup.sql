-- Criação do banco de dados GlicemIA e tabelas iniciais (Executar no SQL Editor do NeonDB)

-- Tipos ENUM
CREATE TYPE tipo_sinal_enum AS ENUM ('GLICEMIA', 'PRESSAO', 'PESO');
CREATE TYPE nivel_risco_enum AS ENUM ('NORMAL', 'ATENCAO', 'ALTO', 'CRITICO');
CREATE TYPE sim_nao_enum AS ENUM ('S', 'N');

-- Tabela de Pacientes
CREATE TABLE TB_PACIENTE (
    id_paciente BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL,
    email VARCHAR(100),
    telefone VARCHAR(20),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Registros de Sinais Vitais
CREATE TABLE TB_REGISTRO (
    id_registro BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_paciente BIGINT NOT NULL,
    tipo_sinal tipo_sinal_enum NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    valor_principal NUMERIC(6,2) NOT NULL,
    valor_secundario NUMERIC(6,2),
    unidade_medida VARCHAR(10) NOT NULL,
    nivel_risco nivel_risco_enum NOT NULL,
    observacoes VARCHAR(500),

    -- Campos específicos de glicemia
    em_jejum sim_nao_enum,
    tipo_insulina VARCHAR(50),

    -- Campos específicos de peso
    altura NUMERIC(3,2),
    imc NUMERIC(4,2),

    CONSTRAINT fk_registro_paciente FOREIGN KEY (id_paciente)
        REFERENCES TB_PACIENTE(id_paciente) ON DELETE CASCADE
);

-- Tabela de Consultas à IA
CREATE TABLE TB_CONSULTA_IA (
    id_consulta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_registro BIGINT NOT NULL,
    data_consulta TIMESTAMP NOT NULL,
    contexto_enviado TEXT,
    resposta_ia TEXT,
    tempo_resposta_ms INTEGER,
    sucesso sim_nao_enum NOT NULL,
    erro VARCHAR(500),

    CONSTRAINT fk_consulta_registro FOREIGN KEY (id_registro)
        REFERENCES TB_REGISTRO(id_registro) ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX idx_registro_paciente ON TB_REGISTRO(id_paciente);
CREATE INDEX idx_registro_data ON TB_REGISTRO(data_hora);
CREATE INDEX idx_registro_risco ON TB_REGISTRO(nivel_risco);
CREATE INDEX idx_consulta_registro ON TB_CONSULTA_IA(id_registro);

-- Comentários nas tabelas (documentação)
COMMENT ON TABLE TB_PACIENTE IS 'Cadastro de pacientes do sistema GlicemIA';
COMMENT ON TABLE TB_REGISTRO IS 'Histórico de medições de sinais vitais';
COMMENT ON TABLE TB_CONSULTA_IA IS 'Log de consultas realizadas à IA generativa';

-- Mensagem de sucesso
DO $$
BEGIN
    RAISE NOTICE 'Database GlicemIA criado com sucesso!';
    RAISE NOTICE 'Tabelas: TB_PACIENTE, TB_REGISTRO, TB_CONSULTA_IA';
END $$;
