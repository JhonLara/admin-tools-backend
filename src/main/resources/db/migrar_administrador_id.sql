-- Agregar columnas administrador_id a empresas y analistas
-- para soportar la asignacion de recursos a usuarios administradores

ALTER TABLE empresas
    ADD COLUMN IF NOT EXISTS administrador_id UUID;

ALTER TABLE analistas
    ADD COLUMN IF NOT EXISTS administrador_id UUID;
